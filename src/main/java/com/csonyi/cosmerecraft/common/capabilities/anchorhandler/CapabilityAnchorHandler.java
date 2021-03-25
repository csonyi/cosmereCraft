package com.csonyi.cosmerecraft.common.capabilities.anchorhandler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityAnchorHandler {
  @CapabilityInject(IAnchorHandler.class)
  public static Capability<IAnchorHandler> ANCHOR_HANDLER_CAPABILITY = null;

  public static void register() {
    CapabilityManager.INSTANCE.register(IAnchorHandler.class, new Storage(), DefaultAnchorHandler::new);
  }

  public static class Storage implements Capability.IStorage<IAnchorHandler> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IAnchorHandler> capability, IAnchorHandler instance, Direction side) {
      ListNBT anchors = new ListNBT();
      for(BlockPos anchor : instance.getAnchors()) {
        anchors.add(NBTUtil.writeBlockPos(anchor));
      }
      return anchors;
    }

    @Override
    public void readNBT(Capability<IAnchorHandler> capability, IAnchorHandler instance, Direction side, INBT nbt) {
      ListNBT anchors = (ListNBT) nbt;

      for(INBT anchor : anchors) {
        instance.addAnchor(NBTUtil.readBlockPos((CompoundNBT) anchor));
      }
    }
  }
}
