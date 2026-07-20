package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.blockentity.AlcoholBoilerBlockEntity;
import com.mrpotato.alcohol_industry.blockentity.JuicePressBlockEntity;
import com.mrpotato.alcohol_industry.blockentity.FermentationBarrelBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, AlcoholIndustry.MOD_ID);
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlcoholBoilerBlockEntity>> ALCOHOL_BOILER =
        BLOCK_ENTITIES.register("alcohol_boiler", () ->
            BlockEntityType.Builder.of(AlcoholBoilerBlockEntity::new, 
                ModBlocks.ALCOHOL_BOILER.get())
            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL =
        BLOCK_ENTITIES.register("fermentation_barrel", () ->
            BlockEntityType.Builder.of(FermentationBarrelBlockEntity::new, 
                ModBlocks.FERMENTATION_BARREL.get())
            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<JuicePressBlockEntity>> JUICE_PRESS =
        BLOCK_ENTITIES.register("juice_press", () ->
            BlockEntityType.Builder.of(JuicePressBlockEntity::new,
                ModBlocks.JUICE_PRESS.get())
            .build(null));
}