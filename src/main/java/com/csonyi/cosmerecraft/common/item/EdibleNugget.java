package com.csonyi.cosmerecraft.common.item;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy.GodMetal;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.csonyi.cosmerecraft.setup.ModSetup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;

public class EdibleNugget extends Item {
  private GodMetal godMetal;

  public EdibleNugget(GodMetal godMetal) {
    super(new Item.Properties()
    .maxStackSize(16)
    .group(ModSetup.COSMERECRAFT_GROUP)
    .rarity(Rarity.EPIC)
    .food(new Food.Builder()
            .setAlwaysEdible()
            .saturation(2)
            .fastToEat()
            .build()
        )
    );
    this.godMetal = godMetal;
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
    if(!(entityLiving instanceof PlayerEntity)) return super.onItemUseFinish(stack, worldIn, entityLiving);
    PlayerEntity player = (PlayerEntity) entityLiving;
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);

    if(godMetal == GodMetal.LERASIUM) {
      for(InvestedMetal metal : InvestedMetal.values()) {
        allomancy.addMetal(metal);
      }
      allomancy.setAtium(true);
    }
    if(godMetal == GodMetal.ATIUM) {
      allomancy.changeAtiumReserve(250F);
    }

    return super.onItemUseFinish(stack, worldIn, entityLiving);
  }
}
