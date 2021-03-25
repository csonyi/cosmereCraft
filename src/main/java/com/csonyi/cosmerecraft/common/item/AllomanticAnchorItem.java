package com.csonyi.cosmerecraft.common.item;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.CapabilityAnchorHandler;
import com.csonyi.cosmerecraft.common.entity.AllomanticAnchorEntity;
import com.csonyi.cosmerecraft.setup.ModSetup;
import com.csonyi.cosmerecraft.setup.Registration;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

public class AllomanticAnchorItem extends Item {
  public AllomanticAnchorItem() {
    super(new Item.Properties()
            .group(ModSetup.COSMERECRAFT_GROUP));
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack held = player.getHeldItem(hand);
    world.playSound(
            null, player.getPosX(), player.getPosY(), player.getPosZ(),
            SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
            0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F)
    );
    if(!held.isEmpty() && !world.isRemote()) {
      AllomanticAnchorEntity anchorEntity = new AllomanticAnchorEntity(player, world);
      anchorEntity.setItem(held);

      // void func_234612_a_() - set motion of the thrown entity
      // Parameters
      //   Entity p_234612_1_ - projectile
      //   float p_234612_2_ - x
      //   float p_234612_3_ - y
      //   float p_234612_4_ - z
      //   float p_234612_5_ - velocity
      //   float p_234612_6_ - inaccuracy
      anchorEntity.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0.0F, 6F, 0F);
      world.addEntity(anchorEntity);
      if(!player.isCreative()) held.shrink(1);
    }
    return ActionResult.resultSuccess(held);
  }
}
