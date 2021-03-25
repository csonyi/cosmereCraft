package com.csonyi.cosmerecraft.common.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.HashMap;
import java.util.Map;

public final class InputHandler {
  private static final Map<PlayerEntity, Boolean> HOLDING_CHARGE = new HashMap<>();
  private static final Map<PlayerEntity, Boolean> HOLDING_HOVER = new HashMap<>();

  public static boolean isHoldingCharge(PlayerEntity player) {
    return HOLDING_CHARGE.containsKey(player) && HOLDING_CHARGE.get(player);
  }

  public static boolean isHoldingHover(PlayerEntity player) {
    return HOLDING_HOVER.containsKey(player) && HOLDING_HOVER.get(player);
  }

  public static void update(PlayerEntity player, boolean jump, boolean sneak) {
    HOLDING_CHARGE.put(player, jump);
    HOLDING_HOVER.put(player, sneak);
  }

  public static void remove(PlayerEntity player) {
    HOLDING_CHARGE.remove(player);
    HOLDING_HOVER.remove(player);
  }

  public static void clear() {
    HOLDING_CHARGE.clear();
    HOLDING_HOVER.clear();
  }

  @SubscribeEvent
  public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    remove(event.getPlayer());
  }

  @SubscribeEvent
  public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
    remove(event.getPlayer());
  }

  @SubscribeEvent
  public void onServerStopping(FMLServerStoppingEvent event) {
    InputHandler.clear();
  }
}
