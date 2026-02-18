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
}
