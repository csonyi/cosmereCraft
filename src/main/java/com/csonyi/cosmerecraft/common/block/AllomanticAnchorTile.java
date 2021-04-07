package com.csonyi.cosmerecraft.common.block;

import com.csonyi.cosmerecraft.setup.Registration;
import net.minecraft.tileentity.TileEntity;

/**
 * Implements the TileEntity for the allomantic anchor Block.
 * No code (yet), because the only purpose this class has is to optimize the scan for nearby anchors.
 * Scanning each block in a 32 block radius around the player would be way too resource intensive.
 */
public class AllomanticAnchorTile extends TileEntity {
  public AllomanticAnchorTile() {
    super(Registration.ALLOMANTIC_ANCHOR_TILE.get());
  }
}
