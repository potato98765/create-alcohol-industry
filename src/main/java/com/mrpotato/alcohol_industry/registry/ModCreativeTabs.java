package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AlcoholIndustry.MOD_ID);
    
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ALCOHOL_INDUSTRY_TAB = 
        CREATIVE_MODE_TABS.register("alcohol_industry", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.alcohol_industry"))
            .icon(() -> new ItemStack(ModItems.ALCOHOL_BOILER_ITEM.get()))
            .displayItems((parameters, output) -> {
                // Machinery
                output.accept(ModItems.ALCOHOL_BOILER_ITEM.get());
                
                // Components
                output.accept(ModItems.GLASS_TUBE.get());
                output.accept(ModItems.TEMPERATURE_CONTROLLER.get());
                
                // Fluid Buckets
                output.accept(ModItems.ALCOHOL_BASE_BUCKET.get());
                output.accept(ModItems.BEER_BUCKET.get());
                output.accept(ModItems.VODKA_BUCKET.get());
                output.accept(ModItems.WHISKEY_BUCKET.get());
                output.accept(ModItems.TEQUILA_BUCKET.get());

                // Drinkable Bottles
                output.accept(ModItems.ALCOHOL_BASE_BOTTLE.get());
                output.accept(ModItems.BEER_BOTTLE.get());
                output.accept(ModItems.VODKA_BOTTLE.get());
                output.accept(ModItems.WHISKEY_BOTTLE.get());
                output.accept(ModItems.TEQUILA_BOTTLE.get());
            })
            .build());
}
