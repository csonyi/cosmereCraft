package com.csonyi.cosmerecraft.common.networking;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.CapabilityAnchorHandler;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.IAnchorHandler;
import com.csonyi.cosmerecraft.common.networking.messages.UpdateAllomancyMessage;
import com.csonyi.cosmerecraft.common.networking.messages.UpdateAnchorsMessage;
import com.csonyi.cosmerecraft.common.networking.messages.UpdateInputMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
  private static int id = 0;
  public static SimpleChannel INSTANCE;

  public static void init() {
    INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CosmereCraft.MOD_ID, "network_channel"),
            () -> "1.0",
            (s) -> true,
            (s) -> true
    );
    INSTANCE.registerMessage(id(), UpdateInputMessage.class, UpdateInputMessage::write, UpdateInputMessage::read, UpdateInputMessage::onMessage);
    INSTANCE.registerMessage(id(), UpdateAllomancyMessage.class, UpdateAllomancyMessage::write, UpdateAllomancyMessage::read, UpdateAllomancyMessage::onMessage);
    INSTANCE.registerMessage(id(), UpdateAnchorsMessage.class, UpdateAnchorsMessage::write, UpdateAnchorsMessage::read, UpdateAnchorsMessage::onMessage);
  }

  private static int id() {
    return id++;
  }

  public static void syncAllomancy(ServerPlayerEntity player) {
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    INSTANCE.send(
            PacketDistributor.PLAYER.with(() -> player),
            new UpdateAllomancyMessage(allomancy)
    );
  }

  public static void syncAnchors(World world) {
    IAnchorHandler anchorHandler = world.getCapability(CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY).orElse(null);
    INSTANCE.send(
            PacketDistributor.ALL.noArg(),
            new UpdateAnchorsMessage(
                    world.getDimensionKey().getLocation(),
                    anchorHandler.getAnchors()
            )
    );
  }

  public static void syncInputs(boolean charging, boolean hovering) {
    INSTANCE.sendToServer(new UpdateInputMessage(charging, hovering));
  }
}
