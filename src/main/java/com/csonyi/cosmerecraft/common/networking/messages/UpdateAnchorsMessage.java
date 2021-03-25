package com.csonyi.cosmerecraft.common.networking.messages;

import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.CapabilityAnchorHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpdateAnchorsMessage {
  private ResourceLocation dimensionType;
  private List<BlockPos> anchors;

  public UpdateAnchorsMessage(ResourceLocation dimensionType, List<BlockPos> anchors) {
    this.anchors = anchors;
    this.dimensionType = dimensionType;
  }

  public static UpdateAnchorsMessage read(PacketBuffer buffer) {
    ResourceLocation dimensionType;
    int numOfAnchors = 0;
    List<BlockPos> anchors = new ArrayList<>();

    dimensionType = buffer.readResourceLocation();

    numOfAnchors = buffer.readInt();
    for(int i = 0; i < numOfAnchors; i++) {
      anchors.add(buffer.readBlockPos());
    }

    return new UpdateAnchorsMessage(dimensionType, anchors);
  }

  public static void write(UpdateAnchorsMessage msg, PacketBuffer buffer) {
    ResourceLocation dimensionType = msg.dimensionType;
    List<BlockPos> anchors = msg.anchors;
    int numOfAnchors = anchors.size();

    buffer.writeResourceLocation(dimensionType);

    buffer.writeInt(numOfAnchors);
    for(BlockPos anchor : anchors) {
      buffer.writeBlockPos(anchor);
    }
  }

  public static void onMessage(UpdateAnchorsMessage msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      RegistryKey<World> worldRegistryKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, msg.dimensionType);
      World world = Minecraft.getInstance().world;

      if(world != null && world.getDimensionKey().getLocation().equals(msg.dimensionType)) {
        world.getCapability(CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY).orElse(null)
                .setAnchors(msg.anchors);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
