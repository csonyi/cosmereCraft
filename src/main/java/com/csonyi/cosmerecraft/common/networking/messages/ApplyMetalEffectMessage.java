package com.csonyi.cosmerecraft.common.networking.messages;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ApplyMetalEffectMessage {
  InvestedMetal metal;
  UUID playerUUID;

  public ApplyMetalEffectMessage(PlayerEntity player, InvestedMetal metal) {
    this(player.getUniqueID(), metal);
  }

  public ApplyMetalEffectMessage(UUID playerUUID, InvestedMetal metal) {
    this.metal = metal;
    this.playerUUID = playerUUID;
  }

  public static ApplyMetalEffectMessage read(PacketBuffer buffer) {
    return new ApplyMetalEffectMessage(buffer.readUniqueId(), buffer.readEnumValue(InvestedMetal.class));
  }

  public static void write(ApplyMetalEffectMessage msg, PacketBuffer buffer) {
    buffer.writeUniqueId(msg.playerUUID);
    buffer.writeEnumValue(msg.metal);
  }

  public static void onMessage(ApplyMetalEffectMessage msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      World world = Minecraft.getInstance().world;
      if(world != null) {
        PlayerEntity player = world.getPlayerByUuid(msg.playerUUID);
        if(player != null) {
          IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
          allomancy.applyMetalEffect(msg.metal, player);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
