package com.csonyi.cosmerecraft.client.screen;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.Resources;
import com.csonyi.cosmerecraft.client.handler.KeybindHandler;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.csonyi.cosmerecraft.common.networking.NetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is the class for the ability selection interface.
 * Clarification:
 *  GR - golden ratio
 *  segment - slice of a circle. [0...7]
 *  level - Inner or outer part of the circle [0...1]
 *  sector - A level and a segment make a sector.
 */
public class MetalSelector extends Screen {
  private static final float GR = 1.61803398875F;
  private static final int SEGMENTS = 8;
  private static final int LEVELS = 2;
  private static final float STEP = (float) Math.PI / 180;
  private static final float DEG_PER_SEGMENT = (float) Math.PI * 2 / SEGMENTS;
  private static final float RADIUS = (float) 160F;
  private static final float SECTOR_MAX_RADIUS[] = {(RADIUS / GR), RADIUS};
  private static final Map<InvestedMetal, ResourceLocation> METAL_TEXTURES = new HashMap<InvestedMetal, ResourceLocation>() {{
    for(Entry<InvestedMetal, String> metal : Resources.METAL_TEXTURES.entrySet()) {
      put(metal.getKey(), new ResourceLocation(CosmereCraft.MOD_ID, metal.getValue()));
    }
  }};
  private static final ResourceLocation LERASIUM_TEXTURE = new ResourceLocation(CosmereCraft.MOD_ID, Resources.LERASIUM_TEXTURE);

  private final Minecraft mc;
  private Set<InvestedMetal> metals;
  private Set<InvestedMetal> burningMetals;
  private int selectedSector[] = {-1, -2};
  private int timeIn = 0;

  public MetalSelector(IAllomancy allomancy) {
    super(new StringTextComponent(""));
    mc = Minecraft.getInstance();
    metals = allomancy.getMetals();
    burningMetals = allomancy.getBurning();
  }

  /**
   * The render function of the GUI.
   * The GUI is made up of the slices of two circles, a smaller and a larger one,
   * with symbols and text rendered on top of them.
   * The color of the sectors depend on whether the associated metal is burning
   * or the mouse is hovering over it.
   * The larger circle gets renders first, then the smaller one, then the text and symbols.
   * TODO: correct terminology
   * @param matrixStack
   * @param mouseX
   * @param mouseY
   * @param partialTicks
   */
  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.render(matrixStack, mouseX, mouseY, partialTicks);

    int screenCenterX = width / 2;
    int screenCenterY = height / 2;
    double mAngle = mouseAngle(screenCenterX, screenCenterY, mouseX, mouseY);
    double mDistance = mouseDistance(screenCenterX, screenCenterY, mouseX, mouseY);

    selectedSector[0] = -1;
    selectedSector[1] = -1;

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();

    RenderSystem.disableCull();
    RenderSystem.disableTexture();
    RenderSystem.enableBlend();
    RenderSystem.shadeModel(GL11.GL_SMOOTH);
    buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

    // Rendering the circles
    for(int level = LEVELS - 1; level >= 0; level--) {
      for(int segment = 0; segment < SEGMENTS; segment++) {
        InvestedMetal metal = InvestedMetal.getMetalFromGuiSector(segment, level);
        boolean mouseInSector = mouseInSector(segment, level, mAngle, mDistance);
        float radius = calculateRadius(segment, level, partialTicks);
        if(mouseInSector || burningMetals.contains(metal)) {
          radius *= 1.025F;
        }

        int grayscaleValue = getSectorColor(segment, level);
        int r = grayscaleValue;
        int g = grayscaleValue;
        int b = grayscaleValue;
        int a = 0x66;

        if(segment == 0) {
          buffer.pos(screenCenterX, screenCenterY, 0).color(r, g, b, a).endVertex();
        }

        if(mouseInSector) {
          selectedSector[0] = segment;
          selectedSector[1] = level;

          r = metal.color.getRed();
          g = metal.color.getGreen();
          b = metal.color.getBlue();
        } else if(burningMetals.contains(metal)) {
          r = metal.color.getRed();
          g = metal.color.getGreen();
          b = metal.color.getBlue();
        }

        for(float i = 0; i < DEG_PER_SEGMENT + STEP / 2; i += STEP) {
          float radians = i + segment * DEG_PER_SEGMENT;
          float sectorRadiusX = screenCenterX + MathHelper.cos(radians) * radius;
          float sectorRadiusY = screenCenterY + MathHelper.sin(radians) * radius;

          if(i == 0) buffer.pos(sectorRadiusX, sectorRadiusY, 0).color(r, g, b, a).endVertex();
          buffer.pos(sectorRadiusX, sectorRadiusY, 0).color(r, g, b, a).endVertex();
        }
      }
    }
    tessellator.draw();


    RenderSystem.shadeModel(GL11.GL_FLAT);
    RenderSystem.enableTexture();

    // Rendering the metal names and textures
    for(int level = LEVELS - 1; level >= 0; level--) {
      for(int segment = 0; segment < SEGMENTS; segment++) {
        InvestedMetal metal = InvestedMetal.getMetalFromGuiSector(segment, level);
        boolean mouseInSector = mouseInSector(segment, level, mAngle, mDistance);
        float radius = calculateRadius(segment, level, partialTicks);
        float radians = (0.5f + segment) * DEG_PER_SEGMENT;

        if(mouseInSector || burningMetals.contains(metal)) {
          radius *= 1.025F;
        }

        String name = metal.name();
        float textRadius = calculateTextRadius(segment, level, partialTicks);
        int textWidthCorrection = font.getStringWidth(name) / 2;
        int textHeightCorrection = font.FONT_HEIGHT / 2;
        int textX = (int) (screenCenterX + MathHelper.cos(radians) * textRadius);
        int textY = (int) (screenCenterY + MathHelper.sin(radians) * textRadius);
        int textXCorrected = textX - textWidthCorrection;
        int textYCorrected = textY - textHeightCorrection;
        float rotation = textRotationForSegment(segment);

        // To rotate the text correctly the render matrix gets translated to the appropriate position,
        // and rotated, and then back after the text is rendered
        // TODO: optimize this, so the matrix isn't moved back to the origo at every iteration
        matrixStack.translate(textX, textY, 0);
        matrixStack.rotate(new Quaternion(0, 0, rotation, false));
        matrixStack.translate(-textX, -textY, 0);
        font.drawStringWithShadow(matrixStack, name, textXCorrected, textYCorrected, metal.color.getRGB());
        matrixStack.translate(textX, textY, 0);
        matrixStack.rotate(new Quaternion(0, 0, -rotation, false));
        matrixStack.translate(-textX, -textY, 0);


        float iconRadius = calculateIconRadius(radius, segment, level, partialTicks);
        int iconX = (int) ((screenCenterX + MathHelper.cos(radians) * iconRadius) - 16);
        int iconY = (int) ((screenCenterY + MathHelper.sin(radians) * iconRadius) - 16);

        ResourceLocation texture = METAL_TEXTURES.getOrDefault(metal, LERASIUM_TEXTURE);
        mc.textureManager.bindTexture(texture);
        blit(matrixStack, iconX, iconY, 0, 0, 32, 32, 32, 32);
      }
    }
  }

  @Override
  public void tick() {
    super.tick();
    if(!isKeyDown(KeybindHandler.openGui)) {
      mc.displayGuiScreen(null);
    }
    timeIn++;
  }

  /**
   * This function handles mouse clicks on the GUI.
   * @param mouseX
   * @param mouseY
   * @param button
   * @return
   */
  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if(selectedSector[0] == -1 || selectedSector[1] == -1) return false;
    PlayerEntity player = mc.player;
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    InvestedMetal metal = InvestedMetal.getMetalFromGuiSector(selectedSector);
    if(!allomancy.hasMetal(metal) || !allomancy.hasReserve(metal)) return false;
    allomancy.toggleBurning(metal);
    return true;
  }

  /**
   * Calculates a central angle between the vector to the mouse pointer, and a point,
   * with the point as the center of the circle. The right-side horizontal radius is considered 0deg
   * increasing clockwise.
   * @param x X coordinate of the center of the circle
   * @param y Y coordinate of the center of the circle
   * @param mouseX
   * @param mouseY
   * @return a value in [0.0...360.0)
   */
  private static double mouseAngle(int x, int y, int mouseX, int mouseY) {
    return (MathHelper.atan2(mouseY - y, mouseX - x) + Math.PI * 2) % (Math.PI * 2);
  }

  /**
   * Calculates the distance of the mouse pointer relative to a point.
   * @param x
   * @param y
   * @param mouseX
   * @param mouseY
   * @return
   */
  private static double mouseDistance(int x, int y, int mouseX, int mouseY) {
    return MathHelper.sqrt(Math.pow(x - mouseX, 2) + Math.pow(y - mouseY, 2));
  }

  /**
   * Function to determine whether mouse is in a sector.
   * @param segment
   * @param level
   * @param mouseAngle
   * @param mouseDistance
   * @return
   */
  private static boolean mouseInSector(int segment, int level, double mouseAngle, double mouseDistance) {
    boolean mouseInSegment = DEG_PER_SEGMENT * segment < mouseAngle
                          && mouseAngle < DEG_PER_SEGMENT * (segment + 1);
    double dist1 = (level == 0) ? 0 : SECTOR_MAX_RADIUS[0];
    double dist2 = SECTOR_MAX_RADIUS[level];

    boolean mouseInLevel = mouseDistance > dist1
                        && mouseDistance < dist2;
    return mouseInSegment && mouseInLevel;
  }

  /**
   * Calculates the alternating grayscale gradient for a sector.
   * @param segment
   * @param level
   * @return
   */
  private int getSectorColor(int segment, int level) {
    int grayscaleValue = 68; // 0x44
    if(segment % 2 == 0) {
      return (level == 0) ? grayscaleValue : grayscaleValue + 25; // 0x19
    }
    return (level == 0) ? grayscaleValue + 25 : grayscaleValue;
  }

  /**
   * Calculates the radius of the current segment and level of the metal selection ring.
   * @param segment
   * @param level
   * @param partialTicks fraction of a tick passed since the last full game tick
   * @return
   */
  private float calculateRadius(int segment, int level, float partialTicks) {
    return Math.max(0F, Math.min((timeIn + partialTicks - segment * 6F / SEGMENTS) * 40F, SECTOR_MAX_RADIUS[level]));
  }

  /**
   * Calculates the radius where the sectors metal icon texture is rendered.
   * @param sectorRadius
   * @param segment
   * @param level
   * @param partialTicks fraction of a tick passed since the last full game tick
   * @return
   */
  private int calculateIconRadius(float sectorRadius, int segment, int level, float partialTicks) {
    return (level == 0)
            ? (int) (sectorRadius / GR)
            : (int) (sectorRadius - (sectorRadius - calculateRadius(segment, 0, partialTicks)) / 2);
  }

  /**
   * Calculates the radius where the sectors metal name is rendered.
   * @param segment
   * @param level
   * @param partialTicks fraction of a tick passed since the last full game tick
   * @return
   */
  private float calculateTextRadius(int segment, int level, float partialTicks) {
    return calculateRadius(segment, 0, partialTicks) + ((level == 0) ? -font.FONT_HEIGHT : font.FONT_HEIGHT);
  }

  /**
   * Helper function for clientside key press detection.
   * @param keybind
   * @return
   */
  private boolean isKeyDown(KeyBinding keybind) {
    InputMappings.Input key = keybind.getKey();
    if (key.getType() == InputMappings.Type.MOUSE) {
      return keybind.isKeyDown();
    }
    return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), key.getKeyCode());
  }

  /**
   * Small function to determine the rotation of the metal names.
   * Improves code readability.
   * @param segment
   * @return
   */
  private float textRotationForSegment(int segment) {
    switch (segment) {
      case 1:
      case 5:
        return -0.5F * DEG_PER_SEGMENT;
      case 0:
      case 4:
        return -1.5F * DEG_PER_SEGMENT;
      case 2:
      case 6:
        return 0.5F * DEG_PER_SEGMENT;
      case 3:
      case 7:
        return 1.5F * DEG_PER_SEGMENT;
      default:
        return 0;
    }
  }
}
