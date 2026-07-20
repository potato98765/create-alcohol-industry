package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.fluid.GenericAlcoholFluidType;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, AlcoholIndustry.MOD_ID);
    
    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> ALCOHOL_BASE_FLUID_TYPE = 
        FLUID_TYPES.register("alcohol_base_fluid", 
            () -> new GenericAlcoholFluidType("alcohol_base", 0xFFF5F5F5, 900, 1200, 300));
    
    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> BEER_TYPE = 
        FLUID_TYPES.register("beer", 
            () -> new GenericAlcoholFluidType("beer", 0xFFD4A574, 1010, 1400, 280));
    
    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> VODKA_TYPE = 
        FLUID_TYPES.register("vodka", 
            () -> new GenericAlcoholFluidType("vodka", 0xFFE8E8E8, 950, 1000, 293));
    
    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> WHISKEY_TYPE = 
        FLUID_TYPES.register("whiskey", 
            () -> new GenericAlcoholFluidType("whiskey", 0xFFAA6B39, 950, 1300, 295));
    
    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> TEQUILA_TYPE = 
        FLUID_TYPES.register("tequila", 
            () -> new GenericAlcoholFluidType("tequila", 0xFFF5E6CC, 945, 1100, 298));

    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> APPLE_JUICE_TYPE = 
        FLUID_TYPES.register("apple_juice", 
            () -> new GenericAlcoholFluidType("apple_juice", 0xFFE4C988, 1020, 1500, 298, "alcohol_base"));

    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> FERMENTED_APPLE_JUICE_TYPE = 
        FLUID_TYPES.register("fermented_apple_juice", 
            () -> new GenericAlcoholFluidType("fermented_apple_juice", 0xFFD8C280, 1015, 1450, 298, "alcohol_base"));

    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> RUM_TYPE = 
        FLUID_TYPES.register("rum", 
            () -> new GenericAlcoholFluidType("rum", 0xFF6A3B16, 960, 1300, 295, "alcohol_base"));


    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> CIDER_TYPE = 
        FLUID_TYPES.register("cider", 
            () -> new GenericAlcoholFluidType("cider", 0xFFD49B3C, 1005, 1100, 298, "alcohol_base"));

    public static final DeferredHolder<FluidType, GenericAlcoholFluidType> APPLE_WINE_TYPE =
        FLUID_TYPES.register("apple_wine",
            () -> new GenericAlcoholFluidType("apple_wine", 0xFF6B0000, 990, 1200, 298, "wine"));
}