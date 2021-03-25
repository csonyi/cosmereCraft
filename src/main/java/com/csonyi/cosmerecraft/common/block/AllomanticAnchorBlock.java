package com.csonyi.cosmerecraft.common.block;

import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.CapabilityAnchorHandler;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.IAnchorHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AllomanticAnchorBlock extends Block implements IWaterLoggable {
  private static final DirectionProperty BLOCK_FACE = HorizontalBlock.HORIZONTAL_FACING;
  private static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
  private static final VoxelShape SHAPE_GROUND_NORTH =
    VoxelShapes.combineAndSimplify(
            Block.makeCuboidShape(8, -0.7693425841512012, 6.834508855262804, 9, 0.23065741584879884, 7.834508855262804),
            Block.makeCuboidShape(8, -0.38665915178611154, 7.758388387774092, 9, 1.6133408482138885, 8.758388387774092),
            IBooleanFunction.OR
    );
  private static final VoxelShape SHAPE_GROUND_EAST =
    VoxelShapes.combineAndSimplify(
            Block.makeCuboidShape(7.383418624556606, -0.12691063726245488, 8, 8.383418624556606, 1.8730893627375451, 9),
            Block.makeCuboidShape(8.307298157067894, -0.5095940696275467, 8, 9.307298157067894, 0.4904059303724533, 9),
            IBooleanFunction.OR
    );
  private static final VoxelShape SHAPE_GROUND_WEST =
    VoxelShapes.combineAndSimplify(
            Block.makeCuboidShape(6.834508855262804, -0.7693425841512012, 7, 7.834508855262804, 0.23065741584879884, 8),
            Block.makeCuboidShape(7.758388387774092, -0.38665915178611154, 7, 8.758388387774092, 1.6133408482138885, 8),
            IBooleanFunction.OR
    );
  private static final VoxelShape SHAPE_GROUND_SOUTH =
    VoxelShapes.combineAndSimplify(
            Block.makeCuboidShape(8, -0.5095940696275467, 8.307298157067894, 9, 0.4904059303724533, 9.307298157067894),
            Block.makeCuboidShape(8, -0.12691063726245488, 7.383418624556606, 9, 1.8730893627375451, 8.383418624556606),
            IBooleanFunction.OR
    );

  public AllomanticAnchorBlock() {
    super(AbstractBlock.Properties
            .create(Material.IRON)
            .hardnessAndResistance(0, 1F)
            .sound(SoundType.CHAIN));
  }

  /**
   * This method is not deprecated,
   * it is marked as such to indicate that if it is needed,
   * it has to be overwritten.
   */
  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    switch (state.get(FACING)) {
      case EAST:
        return SHAPE_GROUND_EAST;
      case WEST:
        return SHAPE_GROUND_WEST;
      case SOUTH:
        return SHAPE_GROUND_SOUTH;
      case NORTH:
      default:
        return SHAPE_GROUND_NORTH;
    }
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
  }

  /**
   * This method is not deprecated,
   * it is marked as such to indicate that if it is needed,
   * it has to be overwritten.
   */
  @SuppressWarnings("deprecation")
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  /**
   * This method is not deprecated,
   * it is marked as such to indicate that if it is needed,
   * it has to be overwritten.
   */
  @SuppressWarnings("deprecation")
  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.toRotation(state.get(FACING)));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new AllomanticAnchorTile();
  }

  @Override
  public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    super.onBlockHarvested(worldIn, pos, state, player);
    if(worldIn.getTileEntity(pos) instanceof AllomanticAnchorTile) {
      IAnchorHandler anchorHandler = getAnchorHandler(worldIn);
      anchorHandler.removeAnchor(pos);
    }
  }

  @Override
  public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
    if(world.getTileEntity(pos) instanceof AllomanticAnchorTile) {
      IAnchorHandler anchorHandler = getAnchorHandler(world);
      anchorHandler.removeAnchor(pos);
    }
  }

  private IAnchorHandler getAnchorHandler(World world) {
    return world.getCapability(CapabilityAnchorHandler.ANCHOR_HANDLER_CAPABILITY).orElse(null);
  }
}
