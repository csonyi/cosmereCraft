package com.csonyi.cosmerecraft.renderer;

import com.csonyi.cosmerecraft.common.block.AllomanticAnchorTile;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;
import com.csonyi.cosmerecraft.setup.Registration;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.OptionalDouble;

/**
 * Renders allomantic lines if specific abilities are activated.
 */
public class AllomanticLineRenderer extends TileEntityRenderer<AllomanticAnchorTile> {
  public AllomanticLineRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
    super(rendererDispatcherIn);
  }

  @Override
  public void render(AllomanticAnchorTile anchor, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
    PlayerEntity player = Minecraft.getInstance().player;
    if(player == null) return;
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    if(!(allomancy.isBurning(InvestedMetal.STEEL) || allomancy.isBurning(InvestedMetal.IRON))) return;

    TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(Blocks.CHAIN.getRegistryName());

    Vector3d playerPos = player.getPositionVec();
    Vector3d anchorPos = new Vector3d(anchor.getPos().getX(), anchor.getPos().getY(), anchor.getPos().getZ());

    RenderType.State renderState = RenderType.State.getBuilder()
            .line(new RenderState.LineState(OptionalDouble.of(1)))
            .layer(ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_239235_M_"))
            .transparency(ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228510_b_"))
            .target(ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_241712_U_"))
            .writeMask(new RenderState.WriteMaskState(true, true))
            .depthTest(new RenderState.DepthTestState("always",GL11.GL_ALWAYS))
            .build(false);
    RenderType myRenderType = RenderType.makeType("mbe_line_1_depth_writing_on",
            DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINES, 128, renderState);
    IVertexBuilder builder = buffer.getBuffer(myRenderType);
    matrixStack.push();

    addLine(builder, matrixStack, playerPos, anchorPos, sprite);

    matrixStack.pop();
  }

  private void addLine(IVertexBuilder renderer, MatrixStack matrixStack, Vector3d playerPos, Vector3d anchorPos, TextureAtlasSprite sprite) {
    Color lineColor = Color.CYAN;
    float cu = 1F / 255F;
    float r = cu * lineColor.getRed();
    float g = cu * lineColor.getGreen();
    float b = cu * lineColor.getBlue();
    float a = 0.5F;
    Vector3d relativePlayerPos = playerPos.subtract(anchorPos);

    renderer.pos(matrixStack.getLast().getMatrix(), 0.5F, 0F, 0.5F)
            .color(r, g, b, a)
            .endVertex();
    renderer.pos(matrixStack.getLast().getMatrix(), (float) relativePlayerPos.x, (float) relativePlayerPos.y, (float) relativePlayerPos.z)
            .color(r, g, b, a)
            .endVertex();
  }

  public static void register() {
    ClientRegistry.bindTileEntityRenderer(Registration.ALLOMANTIC_ANCHOR_TILE.get(), AllomanticLineRenderer::new);
  }
}
