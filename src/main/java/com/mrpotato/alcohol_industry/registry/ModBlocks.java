package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.block.AlcoholBoilerBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(BuiltInRegistries.BLOCK, AlcoholIndustry.MOD_ID);

    private static BlockBehaviour.Properties liquidProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WATER)
                .replaceable()
                .noCollission()
                .strength(100.0F)
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .liquid();
    }

    // Fluid Blocks
    public static final DeferredHolder<Block, LiquidBlock> ALCOHOL_BASE_BLOCK = 
        BLOCKS.register("alcohol_base_fluid_block", () -> new LiquidBlock(ModFluids.ALCOHOL_BASE_SOURCE.get(), liquidProps()));

    public static final DeferredHolder<Block, LiquidBlock> BEER_BLOCK = 
        BLOCKS.register("beer_block", () -> new LiquidBlock(ModFluids.BEER_SOURCE.get(), liquidProps()));

    public static final DeferredHolder<Block, LiquidBlock> VODKA_BLOCK = 
        BLOCKS.register("vodka_block", () -> new LiquidBlock(ModFluids.VODKA_SOURCE.get(), liquidProps()));

    public static final DeferredHolder<Block, LiquidBlock> WHISKEY_BLOCK = 
        BLOCKS.register("whiskey_block", () -> new LiquidBlock(ModFluids.WHISKEY_SOURCE.get(), liquidProps()));

    public static final DeferredHolder<Block, LiquidBlock> TEQUILA_BLOCK = 
        BLOCKS.register("tequila_block", () -> new LiquidBlock(ModFluids.TEQUILA_SOURCE.get(), liquidProps()));
    
    // Alcohol Boiler Block
    public static final DeferredHolder<Block, AlcoholBoilerBlock> ALCOHOL_BOILER =
        BLOCKS.register("alcohol_boiler", () -> new AlcoholBoilerBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
}