package com.csonyi.cosmerecraft.common.item;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.csonyi.cosmerecraft.setup.ModSetup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;

/**
 * Item class for consumable vials of metal powder, used to replenish metal reserves.
 * TODO: Either implement potion effects if crafted with potion, or change inheritance
 */
public class MetalVial extends PotionItem {
  private final InvestedMetal metal;
  public MetalVial(InvestedMetal metal) {
    super(new Item.Properties()
            .group(ModSetup.COSMERECRAFT_GROUP)
            .maxStackSize(1)
            .food(new Food.Builder()
                    .saturation(1)
                    .setAlwaysEdible()
                    .fastToEat()
                    .build()
            )
    );
    this.metal = metal;
  }

  /**
   * When player finishes the consumption of the item, increase reserve of corresponding metal.
   * @param stack ItemStack currently being consumed
   * @param worldIn Current world
   * @param entityLiving LivingEntity consuming the Item
   * @return
   */
  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
    if(!(entityLiving instanceof PlayerEntity)) return super.onItemUseFinish(stack, worldIn, entityLiving);
    PlayerEntity player = (PlayerEntity) entityLiving;
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);

    allomancy.changeReserve(metal, 250F);

    return super.onItemUseFinish(stack, worldIn, entityLiving);
  }
}
