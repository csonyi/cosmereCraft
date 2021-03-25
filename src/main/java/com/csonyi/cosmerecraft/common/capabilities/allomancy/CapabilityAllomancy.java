package com.csonyi.cosmerecraft.common.capabilities.allomancy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.awt.*;

public class CapabilityAllomancy {
  @CapabilityInject(IAllomancy.class)
  public static Capability<IAllomancy> ALLOMANCY_CAPABILITY = null;

  public static void register() {
    CapabilityManager.INSTANCE.register(IAllomancy.class, new Storage(), DefaultAllomancy::new);
  }



  public static class Storage implements Capability.IStorage<IAllomancy> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IAllomancy> capability, IAllomancy instance, Direction side) {
      CompoundNBT tag = new CompoundNBT();

      ListNBT metals = new ListNBT();
      for(InvestedMetal metal : instance.getMetals()) {
        StringNBT metalTag = StringNBT.valueOf(metal.name());
        metals.add(metalTag);
      }

      CompoundNBT reserves = new CompoundNBT();
      for(InvestedMetal metal : InvestedMetal.values()) {
        reserves.putFloat(metal.name(), instance.getReserve(metal));
      }

      ListNBT burning = new ListNBT();
      for(InvestedMetal metal : instance.getBurning()) {
        StringNBT metalTag = StringNBT.valueOf(metal.name());
        burning.add(metalTag);
      }

      CompoundNBT atium = new CompoundNBT();
      atium.putBoolean("hasAtium", instance.hasAtium());
      atium.putFloat("reserve", instance.getAtiumReserve());

      tag.put("metals", metals);
      tag.put("reserves", reserves);
      tag.put("burning", burning);
      tag.put("atium", atium);
      return tag;
    }

    @Override
    public void readNBT(Capability<IAllomancy> capability, IAllomancy instance, Direction side, INBT nbt) {
      CompoundNBT tag = (CompoundNBT) nbt;

      ListNBT metals = (ListNBT) tag.get("metals");
      for(INBT metalTag : metals) {
        StringNBT metalStringTag = (StringNBT) metalTag;
        instance.addMetal(InvestedMetal.valueOf(metalStringTag.getString()));
      }

      CompoundNBT reserves = tag.getCompound("reserves");
      for(InvestedMetal metal : InvestedMetal.values()) {
        instance.setReserve(metal, reserves.getFloat(metal.name()));
      }

      ListNBT burning = (ListNBT) tag.get("burning");
      for(INBT metalTag : burning) {
        StringNBT metalStringTag = (StringNBT) metalTag;
        instance.startBurning(InvestedMetal.valueOf(metalStringTag.getString()));
      }

      CompoundNBT atium = tag.getCompound("atium");
      instance.setAtium(atium.getBoolean("hasAtium"));
      instance.setAtiumReserve(atium.getFloat("reserve"));
    }
  }

  public static IAllomancy getAllomancy(PlayerEntity player) {
    return player.getCapability(ALLOMANCY_CAPABILITY).orElse(null);
  }

  public enum GodMetal {
    ATIUM(Color.LIGHT_GRAY),
    LERASIUM(Color.YELLOW);

    GodMetal(Color color) {
      this.color = color;
    }

    public Color color;

    public TranslationTextComponent toTranslationTextComponent() {
      return new TranslationTextComponent("cosmerecraft.god_metals." + this.name().toLowerCase());
    }
  }
}
