package com.csonyi.cosmerecraft.common.capabilities.anchorhandler;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.common.networking.NetworkHandler;
import com.csonyi.cosmerecraft.common.networking.messages.UpdateAnchorsMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnchorHandlerEventHandler {
  private static final Logger LOGGER = LogManager.getLogger();

  @SubscribeEvent
  public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<World> event) {
    World world = event.getObject();
    AnchorHandlerProvider provider = new AnchorHandlerProvider();

    event.addCapability(new ResourceLocation(CosmereCraft.MOD_ID, "anchor_handler"), provider);
    event.addListener(provider::invalidate);
    LOGGER.info("Attached AnchorHandler capability to world: " + world.getDimensionKey().toString());
  }

  @SubscribeEvent
  public static void onWorldLoad(WorldEvent.Load event) {
    IWorld iWorld = event.getWorld();
    if(iWorld instanceof World) {
      World world = (World) iWorld;
      NetworkHandler.syncAnchors(world);
    }
  }

  @SubscribeEvent
  public static void onWorldSave(WorldEvent.Save event) {
    IWorld iWorld = event.getWorld();
    if(iWorld instanceof World) {
      World world = (World) iWorld;
      NetworkHandler.syncAnchors(world);
    }
  }
}
