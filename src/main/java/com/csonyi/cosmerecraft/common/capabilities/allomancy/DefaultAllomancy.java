package com.csonyi.cosmerecraft.common.capabilities.allomancy;

import com.csonyi.cosmerecraft.common.networking.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO: balance burn rate

/**
 * Default (and the only) implementation of the IAllomancy interface.
 *  This class handles the allomantic abilities of players
 *    non-final class members:
 *      metals - Contains the metallic powers the player has access to
 *      reserves - Contains the reserves for each metal
 *      burningMetals - Contains the metals currently burned by the player
 *      pushPower - Current pushing power for allomantic jumps in [0, 100]
 *      hoverHeight - The y component of the players position for hovering
 *    The metal "Atium" is handled separately from the others. The following members are related to that:
 *      hasAtium - Whether the player has access to Atium
 *      atiumReserve - Current atium reserve
 */
public class DefaultAllomancy implements IAllomancy {
  private static final Logger LOGGER = LogManager.getLogger();
  public final int MAX_PUSH_POWER = 100;
  public final float MAX_RESERVES = 1000F;
  public final float BURN_RATE_PER_TICK = 0.1F;
  public final int EFFECT_DURATION = 15 * 20;

  private final Set<InvestedMetal> metals = new HashSet<>();
  private final Map<InvestedMetal, Float> reserves = new HashMap<>();
  private final Set<InvestedMetal> burningMetals = new HashSet<>();
  private double pushPower = 0;
  private Double hoverHeight = null;

  private boolean hasAtium = false;
  private float atiumReserve = 0F;

  @Override
  public Set<InvestedMetal> getMetals() {
    return metals;
  }

  @Override
  public void setMetals(Set<InvestedMetal> metals) {
    this.metals.addAll(metals);
  }

  @Override
  public boolean hasMetal(InvestedMetal metal) {
    return metals.contains(metal);
  }

  @Override
  public void addMetal(InvestedMetal metal) {
    metals.add(metal);
  }

  @Override
  public void removeMetal(InvestedMetal metal) {
  metals.remove(metal);
  }

  // Atium is handled separately from regular metals

  @Override
  public void setAtium(boolean hasAtium) {
    this.hasAtium = hasAtium;
  }

  @Override
  public void setAtiumReserve(float amount) {
    atiumReserve = Math.max(Math.min(MAX_RESERVES, amount), 0F);
  }

  @Override
  public void changeAtiumReserve(float amount) {
    float newAtiumReserve = atiumReserve + amount;
    atiumReserve = Math.max(Math.min(MAX_RESERVES, newAtiumReserve), 0F);
  }

  @Override
  public float getAtiumReserve() {
    return 0;
  }

  @Override
  public boolean hasAtium() {
    return hasAtium;
  }

  @Override
  public Map<InvestedMetal, Float> getReserves() {
    return reserves;
  }

  @Override
  public void setReserves(Map<InvestedMetal, Float> reserves) {
    this.reserves.clear();
    this.reserves.putAll(reserves);
  }

  @Override
  public float getReserve(InvestedMetal metal) {
    return reserves.getOrDefault(metal, 0F);
  }

  @Override
  public boolean hasReserve(InvestedMetal metal) {
    return getReserve(metal) > 0;
  }

  @Override
  public void setReserve(InvestedMetal metal, float amount) {
    reserves.put(metal, Math.max(Math.min(MAX_RESERVES, amount), 0F));
  }

  @Override
  public void changeReserve(InvestedMetal metal, float amount) {
    float newReserve = getReserve(metal) + amount;
    reserves.put(metal, Math.max(Math.min(MAX_RESERVES, newReserve), 0F));
  }

  @Override
  public void burn(InvestedMetal metal) {
    changeReserve(metal, -BURN_RATE_PER_TICK);
  }

  @Override
  public Set<InvestedMetal> getBurning() {
    return burningMetals;
  }

  @Override
  public void setBurning(Set<InvestedMetal> metals) {
    this.burningMetals.clear();
    this.burningMetals.addAll(metals);
  }

  @Override
  public void startBurning(InvestedMetal metal) {
    burningMetals.add(metal);
  }

  @Override
  public void stopBurning(InvestedMetal metal) {
    burningMetals.remove(metal);
  }

  @Override
  public void toggleBurning(InvestedMetal metal) {
    if(isBurning(metal)) stopBurning(metal);
    else startBurning(metal);
  }

  @Override
  public boolean isBurning(InvestedMetal metal) {
    return burningMetals.contains(metal);
  }

  @Override
  public boolean isPushing() {
    return pushPower > 0;
  }

  @Override
  public double getPushPower() {
    return pushPower;
  }

  @Override
  public void increasePushPower(float amount) {
    pushPower = Math.min(MAX_PUSH_POWER, pushPower + amount);
    LOGGER.debug("Charging: " + pushPower);
  }
  @Override
  public void incrementPushPower() {
    increasePushPower(1);
  }
  @Override
  public void decreasePushPower(float amount) {
    pushPower = Math.max(0, pushPower - amount);
  }
  @Override
  public void decrementPushPower() {
    decreasePushPower(1);
  }


  @Override
  public void applyPush(PlayerEntity player) {
    Vector3d playerMotion = player.getMotion();
    double newMotionY = 0.4F + calculatePushMotion(pushPower);
    player.setMotion(playerMotion.x, newMotionY, playerMotion.z);
    if(player.isSprinting()) {
      player.setMotion(sprintingMotionCorrection(player));
    }
    player.setOnGround(false);
    pushPower = 0;
  }

  @Override
  public void keepFloating(PlayerEntity player) {
    if(player.isOnGround() || hoverHeight == null) return;
    Vector3d playerMotion = player.getMotion();
    double distanceToHoverheight = hoverHeightDistance(player);
    if(Math.abs(distanceToHoverheight) > 0.5) {
    // Player is outside of hover distance
      double newPlayerMotionY = playerMotion.getY();
      // Add/subtract velocity on the y axis depending on the direction of the player acceleration
      newPlayerMotionY += (distanceToHoverheight < 0) ? 0.1D : -0.1D;
      player.setMotion(playerMotion.x, newPlayerMotionY, playerMotion.z);
    } else {
    // Player is in hover distance
      player.setMotion(playerMotion.x, 0, playerMotion.z);
    }
  }

  /**
   * Calculates the applied motion of the "Allomantic Force".
   * Based on an equation by 17th Shard forum user Artemos, from this post:
   * https://www.17thshard.com/forum/topic/71245-making-a-mistborn-video-game-in-unity-–-physics-math-of-allomancy/
   * @param power Current pushing power in the interval [0,100]
   * @return The motion that should be added to the player's motion vector
   */
  private double calculatePushMotion(double power) {
    return 3 * Math.exp(-(32/power));
  }

  private Vector3d sprintingMotionCorrection(PlayerEntity player) {
    Vector3d playerMotion = player.getMotion();
    float f1 = player.rotationYaw * ((float)Math.PI / 180F);
    double pushMultiplier = 0.2F + calculatePushMotion(pushPower);
    return playerMotion.add((-MathHelper.sin(f1) * pushMultiplier), 0.0D, (MathHelper.cos(f1) * pushMultiplier));
  }

  private double hoverHeightDistance(PlayerEntity player) {
    return player.getPosY() - hoverHeight;
  }

  public void calculateHoverHeight(PlayerEntity player) {
    if(player == null) this.hoverHeight = null;
    else hoverHeight = player.getPosY() - player.getMotion().y;
  }

  @Override
  public void applyMetalEffect(InvestedMetal metal, PlayerEntity player) {
    if(player.world.getGameTime() % 80L != 0L) return;
    switch (metal) {
      case PEWTER:
        applyPewterBuff(player);
        break;
      case TIN:
        applyTinBuff(player);
        break;
      case ZINC:
        break;
      case BRASS:
        break;
      case COPPER:
        break;
      case BRONZE:
        break;
      case DURALUMIN:
        break;
      case ALUMINUM:
        break;
      case NICROSIL:
        break;
      case CHROMIUM:
        break;
      case GOLD:
        break;
      case CADMIUM:
        break;
      case ELECTRUM:
        break;
      case BENDALLOY:
        break;
      default:
        break;
    }
  }

  private void applyPewterBuff(PlayerEntity player) {
    int weakAmplifier = (isBurning(InvestedMetal.DURALUMIN)) ? 1 : 0;
    int strongAmplifier = (isBurning(InvestedMetal.DURALUMIN)) ? 4 : 0;
    player.addPotionEffect(new EffectInstance(Effects.STRENGTH, EFFECT_DURATION, strongAmplifier, false, false));
    player.addPotionEffect(new EffectInstance(Effects.SPEED, EFFECT_DURATION, 0, false, false));
    player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, EFFECT_DURATION, weakAmplifier, false, false));
    player.addPotionEffect(new EffectInstance(Effects.HASTE, EFFECT_DURATION, 0, false, false));
    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, EFFECT_DURATION, 0, false, false));
    player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, EFFECT_DURATION, weakAmplifier, false, false));
  }

  private void applyTinBuff(PlayerEntity player) {
    if(isBurning(InvestedMetal.DURALUMIN)) {
      player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, EFFECT_DURATION * 5, 0, false, false));
      player.addPotionEffect(new EffectInstance(Effects.NAUSEA, EFFECT_DURATION * 5, 0, false, false));
    }
    else player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, EFFECT_DURATION, 0, false, false));
  }

  // Speeds up the passing of time
  private void applyCadmiumEffect() {

  }

  // Accelerates TileEntity ticks around the player
  private void applyBendalloyEffect() {

  }

  @Override
  public void update(Set<InvestedMetal> metals, Map<InvestedMetal, Float> reserves,
                     Set<InvestedMetal> burningMetals, boolean hasAtium, float atiumReserve) {
    setMetals(metals);
    setReserves(reserves);
    setBurning(burningMetals);
    setAtium(hasAtium);
    setAtiumReserve(atiumReserve);
  }

  @Override
  public String toString() {
    return "DefaultAllomancy{" +
            "metals=" + metals.toString() +
            ", reserves=" + reserves.toString() +
            '}';
  }
}
