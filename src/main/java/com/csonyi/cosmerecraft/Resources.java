package com.csonyi.cosmerecraft;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;

import java.util.HashMap;
import java.util.Map;

public class Resources {
  public static final String METAL_PREFIX = "textures/icon/metal/";
  public static final Map<InvestedMetal, String> METAL_TEXTURES = new HashMap<InvestedMetal, String>() {{
    put(InvestedMetal.IRON, METAL_PREFIX + "iron_icon.png");
    put(InvestedMetal.STEEL, METAL_PREFIX + "steel_icon.png");
  }};
  public static final String LERASIUM_TEXTURE = METAL_PREFIX + "lerasium_icon.png";
}
