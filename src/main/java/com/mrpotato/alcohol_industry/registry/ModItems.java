package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.item.AlcoholBottleItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(BuiltInRegistries.ITEM, AlcoholIndustry.MOD_ID);
    
    private static Item.Properties bucketProps() {
        return new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1);
    }

    private static Item.Properties bottleProps() {
        return new Item.Properties()
            .stacksTo(1)
            .food(new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build());
    }

    public static final DeferredHolder<Item, BucketItem> ALCOHOL_BASE_BUCKET = 
        ITEMS.register("alcohol_base_bucket", () -> new BucketItem(ModFluids.ALCOHOL_BASE_SOURCE.get(), bucketProps()));

    public static final DeferredHolder<Item, BucketItem> BEER_BUCKET = 
        ITEMS.register("beer_bucket", () -> new BucketItem(ModFluids.BEER_SOURCE.get(), bucketProps()));

    public static final DeferredHolder<Item, BucketItem> VODKA_BUCKET = 
        ITEMS.register("vodka_bucket", () -> new BucketItem(ModFluids.VODKA_SOURCE.get(), bucketProps()));

    public static final DeferredHolder<Item, BucketItem> WHISKEY_BUCKET = 
        ITEMS.register("whiskey_bucket", () -> new BucketItem(ModFluids.WHISKEY_SOURCE.get(), bucketProps()));

    public static final DeferredHolder<Item, BucketItem> TEQUILA_BUCKET = 
        ITEMS.register("tequila_bucket", () -> new BucketItem(ModFluids.TEQUILA_SOURCE.get(), bucketProps()));


    
    public static final DeferredHolder<Item, AlcoholBottleItem> ALCOHOL_BASE_BOTTLE =
        ITEMS.register("alcohol_base", () -> new AlcoholBottleItem(bottleProps()));

    public static final DeferredHolder<Item, AlcoholBottleItem> BEER_BOTTLE =
        ITEMS.register("beer", () -> new AlcoholBottleItem(bottleProps()));

    public static final DeferredHolder<Item, AlcoholBottleItem> VODKA_BOTTLE =
        ITEMS.register("vodka", () -> new AlcoholBottleItem(bottleProps()));

    public static final DeferredHolder<Item, AlcoholBottleItem> WHISKEY_BOTTLE =
        ITEMS.register("whiskey", () -> new AlcoholBottleItem(bottleProps()));

    public static final DeferredHolder<Item, AlcoholBottleItem> TEQUILA_BOTTLE =
        ITEMS.register("tequila", () -> new AlcoholBottleItem(bottleProps()));
        

    
    public static final DeferredHolder<Item, Item> GLASS_TUBE = 
        ITEMS.register("glass_tube", () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> TEMPERATURE_CONTROLLER = 
        ITEMS.register("temperature_controller", () -> new Item(new Item.Properties()));
    

    
    public static final DeferredHolder<Item, BlockItem> ALCOHOL_BOILER_ITEM =
        ITEMS.register("alcohol_boiler", () -> new BlockItem(ModBlocks.ALCOHOL_BOILER.get(), 
            new Item.Properties()));
}
