package com.csonyi.cosmerecraft.client.handler;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.client.screen.MetalSelector;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.csonyi.cosmerecraft.common.handler.InputHandler;
import com.csonyi.cosmerecraft.common.networking.NetworkHandler;
import com.csonyi.cosmerecraft.common.networking.messages.UpdateInputMessage;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * This is where keybindings are created.
 * A keybind is a private static field:
 *  private static KeyBinding keyBinding;
 *  public static void init() {
 *   keyBinding = new KeyBinding("key.registry.name", GLFW.GLFW_KEY_CODE, MOD_ID);
 *   ClientRegistry.registerKeyBinding(keyBinding);
 *  }
 */
public class KeybindHandler {
  public static KeyBinding keyCharging;
  public static KeyBinding keyHovering;
  public static KeyBinding openGui;

  private static boolean charging = false;
  private static boolean hovering = false;

  public static void init() {
    keyCharging = registerKeyBind("keybind.charging", GLFW.GLFW_KEY_SPACE);
    keyHovering = registerKeyBind("keybind.hovering", GLFW.GLFW_KEY_LEFT_SHIFT);
    openGui = registerKeyBind("keybind.gui", GLFW.GLFW_KEY_O);
  }

  /**
   * Event handler responsible for updating the InputHandler class,
   * so movement related inputs are always in sync.
   * @param tickEvent
   */
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent tickEvent) {
    Minecraft mc = Minecraft.getInstance();
    PlayerEntity player = mc.player;
    if(player == null) return;
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    if (mc.getConnection() == null || allomancy == null) return;

    if(tickEvent.phase == TickEvent.Phase.START) {
      boolean chargingNow = keyCharging.isKeyDown();
      boolean hoveringNow = keyHovering.isKeyDown();
      boolean guiOpenNow = openGui.isKeyDown();

      if(chargingNow != charging || hoveringNow != hovering) {
        charging = chargingNow;
        hovering = hoveringNow;

        NetworkHandler.syncInputs(chargingNow, hoveringNow);
        InputHandler.update(player, chargingNow, hoveringNow);
      }

    }
    if(tickEvent.phase == TickEvent.Phase.END) {
      Screen gui = mc.currentScreen;
      if(gui == null && openGui.isKeyDown()) {
        mc.displayGuiScreen(new MetalSelector(allomancy));
      }
    }
  }

  /**
   * Function for registering keybindings..
   * Improves code readability.
   * @param name
   * @param key
   * @return
   */
  private static KeyBinding registerKeyBind(String name, int key) {
    KeyBinding newKeyBind = new KeyBinding(CosmereCraft.MOD_ID + name, key, CosmereCraft.MOD_ID);
    ClientRegistry.registerKeyBinding(newKeyBind);
    return newKeyBind;
  }
}
