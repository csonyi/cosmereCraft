package com.csonyi.cosmerecraft.common.capabilities.allomancy;

import static com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal.*;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.client.handler.KeybindHandler;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.CapabilityAnchorHandler;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.IAnchorHandler;
import com.csonyi.cosmerecraft.common.handler.InputHandler;
import com.csonyi.cosmerecraft.common.networking.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

/**
 * Event handler class for the Allomancy Capability
 */
public class AllomancyEventHandler {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Attaches the Allomantic capability to players
   * @param event
   */
  @SubscribeEvent
  public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
    Entity entity = event.getObject();
    if(entity instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) entity;
      AllomancyProvider provider = new AllomancyProvider();
      event.addCapability(new ResourceLocation(CosmereCraft.MOD_ID, "allomancy"), provider);
      event.addListener(provider::invalidate);
      LOGGER.debug("Attached Capability to Player: " + player.getUniqueID());
    }
  }

  /**
   * Capabilities have to be synced to the client manually, so we do it at login
   * @param event
   */
  @SubscribeEvent
  public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    PlayerEntity player = event.getPlayer();
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    if(player instanceof ServerPlayerEntity) {
      NetworkHandler.syncAllomancy((ServerPlayerEntity) player);
    }
  }

  /**
   * Capabilities have to be synced manually, so we make sure that after the player dies,
   * the new instance gets the old data
   * @param event
   */
  @SubscribeEvent
  public static void onPlayerDeath(PlayerEvent.Clone event) {
    if(!event.isWasDeath()) return;
    PlayerEntity oldPlayer = event.getOriginal();
    PlayerEntity newPlayer = event.getPlayer();

    INBT oldAllomancyNBT = CapabilityAllomancy.ALLOMANCY_CAPABILITY.writeNBT(CapabilityAllomancy.getAllomancy(oldPlayer), null);
    CapabilityAllomancy.ALLOMANCY_CAPABILITY.readNBT(CapabilityAllomancy.getAllomancy(newPlayer), null, oldAllomancyNBT);
  }

  /**
   * This handler is responsible for handling the hovering during allomantic jumping
   * @param event
   */
  @SubscribeEvent
  public static void onHoverKeyEvent(InputEvent.KeyInputEvent event) {
    int keyAction = event.getAction();

    if(event.getKey() != KeybindHandler.keyHovering.getKey().getKeyCode()
    || keyAction == GLFW.GLFW_REPEAT) return;

    PlayerEntity player = Minecraft.getInstance().player;
    if(player == null) return;
    IAnchorHandler anchorHandler = getAnchorHandler(player.getEntityWorld());
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    if(!canPush(player, allomancy, anchorHandler)) return;

    if(keyAction == GLFW.GLFW_PRESS) allomancy.calculateHoverHeight(player);
    if(keyAction == GLFW.GLFW_RELEASE) allomancy.calculateHoverHeight(null);
  }

  /**
   * This handler is responsible for handling the "burning" of metals.
   * If a metallic power is activated (is in the DefaultAllomancy.burningMetals Set),
   * the reserves of the metal have to be decreased.
   * @param event
   */
  @SubscribeEvent
  public static void burningHandler(TickEvent.PlayerTickEvent event) {
    PlayerEntity player = event.player;
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    Set<InvestedMetal> burningMetals = allomancy.getBurning();
    for(InvestedMetal metal : burningMetals) {
      if(allomancy.hasReserve(metal)) {
        if(metal.hasTickingEffect) NetworkHandler.sendMetalEffectPacket(player, metal);
        allomancy.burn(metal);
      } else {
        allomancy.stopBurning(metal);
      }
    }
  }

  /**
   * This handler is responsible for allomantic jumping.
   * Jumps can be charged by holding down the associated key,
   * and executed by releasing it.
   * If for some reason the player isn't able to jump (doesn't have the required metal/has no reserve for it/no anchor in range),
   * they will jump instead.
   * @param event
   */
  @SubscribeEvent
  public static void allomanticJumpHandler(TickEvent.PlayerTickEvent event) {
    PlayerEntity player = event.player;
    boolean isHoldingHover = InputHandler.isHoldingHover(player);
    boolean isHoldingCharge = InputHandler.isHoldingCharge(player);
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);

    if(!(isHoldingHover || isHoldingCharge)
    && !allomancy.isPushing()) return;

    if(!player.getEntityWorld().isRemote) {
      ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
      NetworkHandler.syncAllomancy(serverPlayer);
      return;
    }

    IAnchorHandler anchorHandler = getAnchorHandler(player.getEntityWorld());
    if(!canPush(player, allomancy, anchorHandler)) {
      if(isHoldingCharge && player.isOnGround()) player.jump();
      return;
    }

    if(InputHandler.isHoldingHover(player)) {
      allomancy.keepFloating(player);
    }

    if(InputHandler.isHoldingCharge(player)) {
      allomancy.incrementPushPower();
    } else if(allomancy.isPushing()) {
      allomancy.applyPush(player);
    }
  }

  /**
   * Utility function for getting a World's anchor handler capability.
   * TODO: move to anchor handler capability implementation class
   * @param world
   * @return
   */
  private static IAnchorHandler getAnchorHandler(World world) {
    return world.getCapability(CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY).orElse(null);
  }

  /**
   * Utility function for code readability reasons.
   * @param player
   * @param allomancy
   * @param anchorHandler
   * @return
   */
  private static boolean canPush(PlayerEntity player, IAllomancy allomancy, IAnchorHandler anchorHandler) {
    return allomancy != null
        && anchorHandler != null
        && allomancy.hasMetal(STEEL)
        && allomancy.hasReserve(STEEL)
        && anchorHandler.hasAnchorInRange(player);
  }
}
