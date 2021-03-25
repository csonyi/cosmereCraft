package com.csonyi.cosmerecraft.common.networking.messages;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class UpdateAllomancyMessage {
  private final Set<InvestedMetal> metals;
  private final Set<InvestedMetal> burningMetals;
  private final Map<InvestedMetal, Float> reserves;
  private final boolean hasAtium;
  private final float atiumReserve;


  public UpdateAllomancyMessage(
          Set<InvestedMetal> metals, Map<InvestedMetal, Float> reserves,
          Set<InvestedMetal> burningMetals, boolean hasAtium, float atiumReserve
  ) {
    this.metals = metals;
    this.reserves = reserves;
    this.burningMetals = burningMetals;
    this.hasAtium = hasAtium;
    this.atiumReserve = atiumReserve;
  }

  public UpdateAllomancyMessage(IAllomancy allomancy) {
    this.metals = allomancy.getMetals();
    this.reserves = allomancy.getReserves();
    this.burningMetals = allomancy.getBurning();
    this.hasAtium = allomancy.hasAtium();
    this.atiumReserve = allomancy.getAtiumReserve();
  }

  public static UpdateAllomancyMessage read(PacketBuffer buffer) {
    int numOfMetals = 0;
    int numOfBurning = 0;
    Set<InvestedMetal> metals = new HashSet<>();
    Map<InvestedMetal, Float> reserves = new HashMap<>();
    Set<InvestedMetal> burningMetals = new HashSet<>();
    boolean hasAtium = false;
    float atiumReserve = 0F;

    numOfMetals = buffer.readInt();
    for(int i = 0; i < numOfMetals; i++) {
      metals.add(buffer.readEnumValue(InvestedMetal.class));
    }

    for(InvestedMetal metal : InvestedMetal.values()) {
      float currentMetalReserve = buffer.readFloat();
      reserves.put(metal, currentMetalReserve);
    }

    numOfBurning = buffer.readInt();
    for(int i = 0; i < numOfBurning; i++) {
      burningMetals.add(buffer.readEnumValue(InvestedMetal.class));
    }

    hasAtium = buffer.readBoolean();
    atiumReserve = buffer.readFloat();

    return new UpdateAllomancyMessage(metals, reserves, burningMetals, hasAtium, atiumReserve);
  }

  public static void write(UpdateAllomancyMessage msg, PacketBuffer buffer) {
    Set<InvestedMetal> metals = msg.metals;
    int numOfMetals = metals.size();
    Map<InvestedMetal, Float> reserves = msg.reserves;
    Set<InvestedMetal> burningMetals = msg.burningMetals;
    int numOfBurning = burningMetals.size();

    buffer.writeInt(numOfMetals);
    for(InvestedMetal metal : metals) {
      buffer.writeEnumValue(metal);
    }

    for(InvestedMetal metal : InvestedMetal.values()) {
      float currentMetalReserve = reserves.get(metal);
      buffer.writeFloat(currentMetalReserve);
    }

    buffer.writeInt(numOfBurning);
    for(InvestedMetal metal : burningMetals) {
      buffer.writeEnumValue(metal);
    }

    buffer.writeBoolean(msg.hasAtium);
    buffer.writeFloat(msg.atiumReserve);
  }

  public static void onMessage(UpdateAllomancyMessage msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      PlayerEntity player = Minecraft.getInstance().player;
      if(player != null) {
        player.getCapability(CapabilityAllomancy.ALLOMANCY_CAPABILITY).orElse(null)
                .update(msg.metals, msg.reserves, msg.burningMetals, msg.hasAtium, msg.atiumReserve);
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
