package com.csonyi.cosmerecraft;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;

import java.util.HashMap;
import java.util.Map;

/**
 * Resource storage
 */
public class Resources {
  public static final String METAL_PREFIX = "textures/icon/metal/";
  public static final Map<InvestedMetal, String> METAL_TEXTURES = new HashMap<InvestedMetal, String>() {{
    put(InvestedMetal.STEEL, METAL_PREFIX + "steel_icon.png");
    put(InvestedMetal.IRON, METAL_PREFIX + "iron_icon.png");
    put(InvestedMetal.PEWTER, METAL_PREFIX + "pewter_icon.png");
    put(InvestedMetal.TIN, METAL_PREFIX + "tin_icon.png");

    put(InvestedMetal.ZINC, METAL_PREFIX + "zinc_icon.png");
    put(InvestedMetal.BRASS, METAL_PREFIX + "brass_icon.png");
    put(InvestedMetal.COPPER, METAL_PREFIX + "copper_icon.png");
    put(InvestedMetal.BRONZE, METAL_PREFIX + "bronze_icon.png");

    put(InvestedMetal.DURALUMIN, METAL_PREFIX + "duralumin_icon.png");
    put(InvestedMetal.ALUMINUM, METAL_PREFIX + "aluminum_icon.png");
    put(InvestedMetal.NICROSIL, METAL_PREFIX + "nicrosil_icon.png");
    put(InvestedMetal.CHROMIUM, METAL_PREFIX + "chromium_icon.png");

    put(InvestedMetal.GOLD, METAL_PREFIX + "gold_icon.png");
    put(InvestedMetal.CADMIUM, METAL_PREFIX + "cadmium_icon.png");
    put(InvestedMetal.ELECTRUM, METAL_PREFIX + "electrum_icon.png");
    put(InvestedMetal.BENDALLOY, METAL_PREFIX + "bendalloy_icon.png");
  }};
  public static final String LERASIUM_TEXTURE = METAL_PREFIX + "lerasium_icon.png";
}
