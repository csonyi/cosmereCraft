package com.csonyi.cosmerecraft.common.capabilities.anchorhandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IAnchorHandler {
  void addAnchor(BlockPos pos);
  void removeAnchor(BlockPos pos);
  List<BlockPos> getAnchors();
  void setAnchors(List<BlockPos> anchors);

  boolean hasAnchorInRange(PlayerEntity player);
}
