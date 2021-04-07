package com.csonyi.cosmerecraft.common.networking.messages;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class UpdateAllomancyMessage {
  CompoundNBT allomancyNBT;

  public UpdateAllomancyMessage(IAllomancy allomancy) {
    allomancyNBT = CapabilityAllomancy.write(allomancy);
  }

  public UpdateAllomancyMessage(CompoundNBT allomancyNBT) {
    this.allomancyNBT = allomancyNBT;
  }

  public static UpdateAllomancyMessage read(PacketBuffer buffer) {
    return new UpdateAllomancyMessage(buffer.readCompoundTag());
  }

  public static void write(UpdateAllomancyMessage msg, PacketBuffer buffer) {
    buffer.writeCompoundTag(msg.allomancyNBT);
  }

  public static void onMessage(UpdateAllomancyMessage msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      PlayerEntity player = Minecraft.getInstance().player;
      if(player != null) {
        IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
        CapabilityAllomancy.read(allomancy, msg.allomancyNBT);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
