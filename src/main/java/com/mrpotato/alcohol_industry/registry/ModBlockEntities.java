package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.blockentity.AlcoholBoilerBlockEntity;
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
}