package com.csonyi.cosmerecraft.common.capabilities.anchorhandler;

import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnchorHandlerProvider implements ICapabilitySerializable<ListNBT> {
  private final DefaultAnchorHandler anchorHandler = new DefaultAnchorHandler();
  private final LazyOptional<IAnchorHandler> anchorHandlerOptional = LazyOptional.of(() -> anchorHandler);

  public void invalidate() {
    anchorHandlerOptional.invalidate();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
    return anchorHandlerOptional.cast();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return getCapability(cap);
  }

  @Override
  public ListNBT serializeNBT() {
    if(CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY == null) return new ListNBT();
    return (ListNBT) CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY.writeNBT(anchorHandler, null);
  }

  @Override
  public void deserializeNBT(ListNBT nbt) {
    if(CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY != null) CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY.readNBT(anchorHandler, null, nbt);
  }
}
