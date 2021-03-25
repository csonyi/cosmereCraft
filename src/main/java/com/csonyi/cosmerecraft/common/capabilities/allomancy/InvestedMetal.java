package com.csonyi.cosmerecraft.common.capabilities.allomancy;

import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

// TODO: DOCS
/**
 * Represents the Metals used in the Metallic Arts.
 * Physical Metals
 *  STEEL  - Used for pushing metals
 *  IRON   - Used for pushing metals
 *  PEWTER - Increases physical strength
 *  TIN    - Increases senses
 *
 * Temporal Metals
 *  CADMIUM   - Slow down time
 *  BENDALLOY - Speed up time
 */
public enum InvestedMetal {
  // Physical Metals
  STEEL(Color.GRAY), IRON(Color.LIGHT_GRAY),
  PEWTER(Color.GRAY, true), TIN(Color.LIGHT_GRAY, true),

  // Mental Metals
  ZINC(Color.LIGHT_GRAY), BRASS(Color.ORANGE),
  COPPER(Color.ORANGE), BRONZE(Color.ORANGE),

  // Enhancement Metals
  DURALUMIN(Color.LIGHT_GRAY), ALUMINUM(Color.LIGHT_GRAY),
  NICROSIL(Color.DARK_GRAY), CHROMIUM(Color.CYAN),

  // Temporal Metals
  GOLD(Color.YELLOW), CADMIUM(Color.GRAY),
  ELECTRUM(Color.ORANGE), BENDALLOY(Color.GRAY);

  public final Color color;
  public final boolean hasTickingEffect;
  private final static InvestedMetal[][] sectorTable = {
          {ELECTRUM, BENDALLOY},
          {GOLD, CADMIUM},
          {DURALUMIN, NICROSIL},
          {ALUMINUM, CHROMIUM},
          {TIN, IRON},
          {PEWTER, STEEL},
          {BRASS, ZINC},
          {BRONZE, COPPER}
  };

  InvestedMetal(Color color, boolean hasTickingEffect) {
    this.color = color;
    this.hasTickingEffect = hasTickingEffect;
  }

  InvestedMetal(Color color) {
    this(color, false);
  }

  public TranslationTextComponent toTranslationTextComponent() {
    return new TranslationTextComponent("cosmerecraft.metals." + this.name().toLowerCase());
  }

  public static InvestedMetal getMetalFromGuiSector(int[] sector) {
    return sectorTable[sector[0]][sector[1]];
  }

  public static InvestedMetal getMetalFromGuiSector(int segment, int level) {
    return sectorTable[segment][level];
  }
}
