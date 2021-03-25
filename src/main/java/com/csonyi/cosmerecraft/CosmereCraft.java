package com.csonyi.cosmerecraft;

import com.csonyi.cosmerecraft.setup.ClientSetup;
import com.csonyi.cosmerecraft.setup.ModSetup;
import com.csonyi.cosmerecraft.setup.Registration;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CosmereCraft.MOD_ID)
public class CosmereCraft {
    public static final String MOD_ID = "cosmerecraft";

    public CosmereCraft() {
        Registration.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModSetup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
