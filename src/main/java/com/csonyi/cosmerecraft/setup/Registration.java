package com.csonyi.cosmerecraft.setup;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.common.block.AllomanticAnchorBlock;
import com.csonyi.cosmerecraft.common.block.AllomanticAnchorTile;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.entity.AllomanticAnchorEntity;
import com.csonyi.cosmerecraft.common.item.AllomanticAnchorItem;
import com.csonyi.cosmerecraft.common.item.EdibleNugget;
import com.csonyi.cosmerecraft.common.item.MetalVial;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registration {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CosmereCraft.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CosmereCraft.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, CosmereCraft.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, CosmereCraft.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CosmereCraft.MOD_ID);

    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        ENTITY_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
        TILE_ENTITY_TYPES.register(eventBus);
    }

    // Items

    //  Dusts
    public static final RegistryObject<Item> COAL_DUST =
            ITEMS.register("coal_dust",
                    () -> new Item(itemProperties()));

    public static final RegistryObject<Item> IRON_DUST =
          ITEMS.register("iron_dust",
                  () -> new Item(itemProperties())
          );

    public static final RegistryObject<Item> STEEL_DUST =
          ITEMS.register("steel_dust",
                  () -> new Item(itemProperties())
          );


    // Small Dusts
    public static final RegistryObject<Item> SMALL_IRON_DUST =
            ITEMS.register("small_iron_dust",
                    () -> new Item(itemProperties())
            );

    public static final RegistryObject<Item> SMALL_STEEL_DUST =
          ITEMS.register("small_steel_dust",
                  () -> new Item(itemProperties())
          );


    // Ingots
    public static final RegistryObject<Item> STEEL_INGOT =
          ITEMS.register("steel_ingot",
                  () -> new Item(itemProperties())
          );


    // Nuggets
    public static final RegistryObject<Item> STEEL_NUGGET =
          ITEMS.register("steel_nugget",
                  () -> new Item(itemProperties())
          );

    public static final RegistryObject<Item> LERASIUM_NUGGET =
            ITEMS.register("lerasium_nugget",
                    () -> new EdibleNugget(CapabilityAllomancy.GodMetal.LERASIUM));

    public static final RegistryObject<Item> ALLOMANTIC_ANCHOR_ITEM =
            ITEMS.register("allomantic_anchor_item",
                    AllomanticAnchorItem::new
            );

    public static final RegistryObject<Item> IRON_DUST_BOTTLE =
            ITEMS.register("iron_dust_bottle",
                    () -> new MetalVial(InvestedMetal.IRON)
            );

    public static final RegistryObject<Item> STEEL_DUST_BOTTLE =
            ITEMS.register("steel_dust_bottle",
                    () -> new MetalVial(InvestedMetal.STEEL)
            );


    // Blocks & Block Items
    public static final RegistryObject<Block> ALLOMANTIC_ANCHOR_BLOCK =
            BLOCKS.register("allomantic_anchor_block",
                    AllomanticAnchorBlock::new
            );

    public static final RegistryObject<Item> ALLOMANTIC_ANCHOR_BLOCK_ITEM =
            ITEMS.register(
                    "allomantic_anchor_block",
                    () -> new BlockItem(ALLOMANTIC_ANCHOR_BLOCK.get(),
                    new Item.Properties().group(ModSetup.COSMERECRAFT_GROUP))
            );

    public static final RegistryObject<EntityType<AllomanticAnchorEntity>> ALLOMANTIC_ANCHOR_ENTITY_TYPE =
            ENTITY_TYPES.register(
                    "allomantic_anchor_entity",
                    () -> EntityType.Builder.<AllomanticAnchorEntity>create(AllomanticAnchorEntity::new, EntityClassification.MISC)
                            .size((1f/16f), (1f/16f))
                            .immuneToFire()
                            .build("allomantic_anchor_entity")
            );

    public static final RegistryObject<TileEntityType<AllomanticAnchorTile>> ALLOMANTIC_ANCHOR_TILE =
            TILE_ENTITY_TYPES.register(
                    "allomantic_anchor",
                    () -> TileEntityType.Builder.create(AllomanticAnchorTile::new, ALLOMANTIC_ANCHOR_BLOCK.get())
                            .build(null)
            );

    private static Item.Properties itemProperties() {
      return new Item.Properties().group(ModSetup.COSMERECRAFT_GROUP);
    }
}
