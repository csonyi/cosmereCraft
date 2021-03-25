package com.csonyi.cosmerecraft.setup;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.commands.CosmereCraftCommands;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.AllomancyEventHandler;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.AnchorHandlerEventHandler;
import com.csonyi.cosmerecraft.common.capabilities.anchorhandler.CapabilityAnchorHandler;
import com.csonyi.cosmerecraft.common.handler.InputHandler;
import com.csonyi.cosmerecraft.common.networking.NetworkHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(
        modid = CosmereCraft.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ModSetup {
  public static final ItemGroup COSMERECRAFT_GROUP = new ItemGroup("cosmerecraft") {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(Registration.ALLOMANTIC_ANCHOR_ITEM.get());
    }
  };

  public static void init(final FMLCommonSetupEvent event) {
    CapabilityAllomancy.register();
    CapabilityAnchorHandler.register();

    MinecraftForge.EVENT_BUS.register(new InputHandler());
    MinecraftForge.EVENT_BUS.register(AllomancyEventHandler.class);
    MinecraftForge.EVENT_BUS.register(AnchorHandlerEventHandler.class);

    event.enqueueWork(NetworkHandler::init);
  }

  @SubscribeEvent
  public static void onCommandRegistration(RegisterCommandsEvent event) {
    CosmereCraftCommands.register(event.getDispatcher());
  }
}
