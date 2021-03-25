package com.csonyi.cosmerecraft.common.capabilities.allomancy;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.DefaultAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AllomancyProvider implements ICapabilitySerializable<CompoundNBT> {
  private final DefaultAllomancy allomancy = new DefaultAllomancy();
  private final LazyOptional<IAllomancy> allomancyOptional = LazyOptional.of(() -> allomancy);

  public void invalidate() {
    allomancyOptional.invalidate();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
    return allomancyOptional.cast();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return getCapability(cap);
  }

  @Override
  public CompoundNBT serializeNBT() {
    if(CapabilityAllomancy.ALLOMANCY_CAPABILITY == null) return new CompoundNBT();
    return (CompoundNBT) CapabilityAllomancy.ALLOMANCY_CAPABILITY.writeNBT(allomancy, null);
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    if(CapabilityAllomancy.ALLOMANCY_CAPABILITY != null) CapabilityAllomancy.ALLOMANCY_CAPABILITY.readNBT(allomancy, null, nbt);
  }
}
