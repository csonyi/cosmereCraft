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

public class AllomancyEventHandler {
  private static final Logger LOGGER = LogManager.getLogger();

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

  @SubscribeEvent
  public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    PlayerEntity player = event.getPlayer();
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    if(player instanceof ServerPlayerEntity) {
      NetworkHandler.syncAllomancy((ServerPlayerEntity) player);
    }
  }

  @SubscribeEvent
  public static void onPlayerDeath(PlayerEvent.Clone event) {
    if(!event.isWasDeath()) return;
    PlayerEntity oldPlayer = event.getOriginal();
    PlayerEntity newPlayer = event.getPlayer();

    INBT oldAllomancyNBT = CapabilityAllomancy.ALLOMANCY_CAPABILITY.writeNBT(CapabilityAllomancy.getAllomancy(oldPlayer), null);
    CapabilityAllomancy.ALLOMANCY_CAPABILITY.readNBT(CapabilityAllomancy.getAllomancy(newPlayer), null, oldAllomancyNBT);
  }

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

  @SubscribeEvent
  public static void burningHandler(TickEvent.PlayerTickEvent event) {
    PlayerEntity player = event.player;
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    Set<InvestedMetal> burningMetals = allomancy.getBurning();
    for(InvestedMetal metal : burningMetals) {
      if(allomancy.hasReserve(metal)) {
        if(metal.hasTickingEffect) allomancy.applyMetalEffect(metal, player);
        allomancy.burn(metal);
      } else {
        allomancy.stopBurning(metal);
      }
    }
    allomancy.tickEffects();
  }

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

  private static IAnchorHandler getAnchorHandler(World world) {
    return world.getCapability(CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY).orElse(null);
  }

  private static boolean canPush(PlayerEntity player, IAllomancy allomancy, IAnchorHandler anchorHandler) {
    return allomancy != null
        && anchorHandler != null
        && allomancy.hasMetal(STEEL)
        && allomancy.hasReserve(STEEL)
        && anchorHandler.hasAnchorInRange(player);
  }
}
