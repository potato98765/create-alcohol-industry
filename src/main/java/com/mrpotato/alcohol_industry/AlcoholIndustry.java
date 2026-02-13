package com.mrpotato.alcohol_industry;

import com.mrpotato.alcohol_industry.blockentity.AlcoholBoilerBlockEntity;
import com.mrpotato.alcohol_industry.registry.*;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AlcoholIndustry.MOD_ID)
public class AlcoholIndustry {

    public static final String MOD_ID = "alcohol_industry";
    public static final String NAME = "Alcohol Industry";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public AlcoholIndustry(IEventBus modEventBus) {
        LOGGER.info("Initializing Alcohol Industry mod...");

        // Registries
        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        // ✅ Capability registration (CORRECT way)
        modEventBus.addListener(this::registerCaps);

        LOGGER.info("Alcohol Industry mod initialized successfully!");
    }

    private void registerCaps(RegisterCapabilitiesEvent event) {
        AlcoholBoilerBlockEntity.registerCapabilities(event);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
