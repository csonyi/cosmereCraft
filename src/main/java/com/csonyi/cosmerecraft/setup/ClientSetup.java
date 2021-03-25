package com.csonyi.cosmerecraft.setup;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.client.handler.KeybindHandler;
import com.csonyi.cosmerecraft.renderer.AllomanticLineRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = CosmereCraft.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ClientSetup {
  public static void init(final FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(
            Registration.ALLOMANTIC_ANCHOR_ENTITY_TYPE.get(),
            erm -> new SpriteRenderer<>(erm, Minecraft.getInstance().getItemRenderer())
    );

    MinecraftForge.EVENT_BUS.register(new KeybindHandler());
    AllomanticLineRenderer.register();
    KeybindHandler.init();
  }
}
