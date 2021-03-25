package com.csonyi.cosmerecraft.common.networking.messages;

import com.csonyi.cosmerecraft.common.handler.InputHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateInputMessage {
  private final boolean jumping;
  private final boolean sneaking;

  public UpdateInputMessage(boolean jumping, boolean sneaking) {
    this.jumping = jumping;
    this.sneaking = sneaking;
  }

  public static UpdateInputMessage read(PacketBuffer buffer) {
    return new UpdateInputMessage(buffer.readBoolean(), buffer.readBoolean());
  }

  public static void write(UpdateInputMessage msg, PacketBuffer buffer) {
    buffer.writeBoolean(msg.jumping);
    buffer.writeBoolean(msg.sneaking);
  }

  public static void onMessage(UpdateInputMessage msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      PlayerEntity player = ctx.get().getSender();
      if(player != null) InputHandler.update(player, msg.jumping, msg.sneaking);
    });
    ctx.get().setPacketHandled(true);
  }
}
