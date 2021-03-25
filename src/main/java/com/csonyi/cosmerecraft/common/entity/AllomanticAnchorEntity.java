package com.csonyi.cosmerecraft.common.entity;

import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.CapabilityAnchorHandler;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.IAnchorHandler;
import com.csonyi.cosmerecraft.common.networking.NetworkHandler;
import com.csonyi.cosmerecraft.common.networking.messages.UpdateAnchorsMessage;
import com.csonyi.cosmerecraft.setup.Registration;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketDirection;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AllomanticAnchorEntity extends ProjectileItemEntity {
  private static final Logger LOGGER = LogManager.getLogger();

  public AllomanticAnchorEntity(double x, double y, double z, World worldIn) {
    super(Registration.ALLOMANTIC_ANCHOR_ENTITY_TYPE.get(), x, y, z, worldIn);
  }

  public AllomanticAnchorEntity(LivingEntity livingEntityIn, World worldIn) {
    super(Registration.ALLOMANTIC_ANCHOR_ENTITY_TYPE.get(), livingEntityIn, worldIn);
  }

  public AllomanticAnchorEntity(EntityType<? extends ProjectileItemEntity> type, World world) {
    super(Registration.ALLOMANTIC_ANCHOR_ENTITY_TYPE.get(), world);
  }

  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  protected Item getDefaultItem() {
    return Registration.ALLOMANTIC_ANCHOR_ITEM.get();
  }

  @Override
  protected void onImpact(RayTraceResult result) {
    // Get the shooter entity
    PlayerEntity shooter;
    if(this.func_234616_v_() instanceof PlayerEntity) shooter = (PlayerEntity) this.func_234616_v_();
    else return;
    if(shooter == null) return;

    if(result.getType() == RayTraceResult.Type.ENTITY) {
      EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) result;
      Entity impactedEntity = entityRayTraceResult.getEntity();
      if(impactedEntity instanceof LivingEntity) {
        LivingEntity livingEntity = (LivingEntity) impactedEntity;
        DamageSource damageSource = DamageSource.causeIndirectDamage(this, shooter);
        livingEntity.attackEntityFrom(damageSource, 6F);
      }
    }

    if(!this.world.isRemote() || result.getType() == RayTraceResult.Type.BLOCK) {
      BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) result;
      BlockPos impactedBlock = blockRayTraceResult.getPos();
      Direction impactedFace = blockRayTraceResult.getFace();
      BlockPos placementPos = impactedBlock.offset(impactedFace);
      world.setBlockState(
              placementPos,
              Registration.ALLOMANTIC_ANCHOR_BLOCK.get().getDefaultState().with(
                      HorizontalBlock.HORIZONTAL_FACING,
                      shooter.getHorizontalFacing()
              )
      );
      this.remove();
      IAnchorHandler anchorHandler = world.getCapability(CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY).orElse(null);
      anchorHandler.addAnchor(placementPos);
      NetworkHandler.syncAnchors(world);
    }
  }

  @Override
  protected void registerData() {
    super.registerData();
  }
}
