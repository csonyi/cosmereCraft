package com.csonyi.cosmerecraft.common.capabilities.anchorhandler;

import com.csonyi.cosmerecraft.common.block.AllomanticAnchorTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DefaultAnchorHandler implements IAnchorHandler {
  private static final Logger LOGGER = LogManager.getLogger();
  private List<BlockPos> anchors = new ArrayList<>();

  @Override
  public void addAnchor(BlockPos pos) {
    anchors.add(pos);
  }

  @Override
  public void removeAnchor(BlockPos pos) {
    anchors.remove(pos);
  }

  @Override
  public List<BlockPos> getAnchors() {
    return anchors;
  }

  @Override
  public void setAnchors(List<BlockPos> anchors) {
    this.anchors = anchors;
  }

  @Override
  public boolean hasAnchorInRange(PlayerEntity player) {
    return anchors.stream().anyMatch((anchor) -> anchor.withinDistance(player.getPositionVec(), 64));
  }
}
