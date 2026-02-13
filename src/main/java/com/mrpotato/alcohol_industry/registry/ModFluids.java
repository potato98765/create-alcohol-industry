package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = 
        DeferredRegister.create(BuiltInRegistries.FLUID, AlcoholIndustry.MOD_ID);

    // Alcohol Base
    public static final DeferredHolder<Fluid, FlowingFluid> ALCOHOL_BASE_SOURCE = 
        FLUIDS.register("alcohol_base_fluid", () -> new BaseFlowingFluid.Source(ModFluids.ALCOHOL_BASE_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> ALCOHOL_BASE_FLOWING = 
        FLUIDS.register("alcohol_base_fluid_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.ALCOHOL_BASE_PROPERTIES));
    public static final BaseFlowingFluid.Properties ALCOHOL_BASE_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.ALCOHOL_BASE_FLUID_TYPE, ALCOHOL_BASE_SOURCE, ALCOHOL_BASE_FLOWING)
            .bucket(ModItems.ALCOHOL_BASE_BUCKET).block(ModBlocks.ALCOHOL_BASE_BLOCK);

    // Beer
    public static final DeferredHolder<Fluid, FlowingFluid> BEER_SOURCE = 
        FLUIDS.register("beer", () -> new BaseFlowingFluid.Source(ModFluids.BEER_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> BEER_FLOWING = 
        FLUIDS.register("beer_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.BEER_PROPERTIES));
    public static final BaseFlowingFluid.Properties BEER_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.BEER_TYPE, BEER_SOURCE, BEER_FLOWING)
            .bucket(ModItems.BEER_BUCKET).block(ModBlocks.BEER_BLOCK);

    // Vodka
    public static final DeferredHolder<Fluid, FlowingFluid> VODKA_SOURCE = 
        FLUIDS.register("vodka", () -> new BaseFlowingFluid.Source(ModFluids.VODKA_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> VODKA_FLOWING = 
        FLUIDS.register("vodka_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.VODKA_PROPERTIES));
    public static final BaseFlowingFluid.Properties VODKA_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.VODKA_TYPE, VODKA_SOURCE, VODKA_FLOWING)
            .bucket(ModItems.VODKA_BUCKET).block(ModBlocks.VODKA_BLOCK);

    // Whiskey
    public static final DeferredHolder<Fluid, FlowingFluid> WHISKEY_SOURCE = 
        FLUIDS.register("whiskey", () -> new BaseFlowingFluid.Source(ModFluids.WHISKEY_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> WHISKEY_FLOWING = 
        FLUIDS.register("whiskey_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.WHISKEY_PROPERTIES));
    public static final BaseFlowingFluid.Properties WHISKEY_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.WHISKEY_TYPE, WHISKEY_SOURCE, WHISKEY_FLOWING)
            .bucket(ModItems.WHISKEY_BUCKET).block(ModBlocks.WHISKEY_BLOCK);

    // Tequila
    public static final DeferredHolder<Fluid, FlowingFluid> TEQUILA_SOURCE = 
        FLUIDS.register("tequila", () -> new BaseFlowingFluid.Source(ModFluids.TEQUILA_PROPERTIES));
    public static final DeferredHolder<Fluid, FlowingFluid> TEQUILA_FLOWING = 
        FLUIDS.register("tequila_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.TEQUILA_PROPERTIES));
    public static final BaseFlowingFluid.Properties TEQUILA_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.TEQUILA_TYPE, TEQUILA_SOURCE, TEQUILA_FLOWING)
            .bucket(ModItems.TEQUILA_BUCKET).block(ModBlocks.TEQUILA_BLOCK);
}
