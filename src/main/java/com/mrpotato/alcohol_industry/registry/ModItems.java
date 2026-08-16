package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.item.AlcoholBottleItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

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

    public static final DeferredHolder<Item, BucketItem> APPLE_JUICE_BUCKET = 
        ITEMS.register("apple_juice_bucket", () -> new BucketItem(ModFluids.APPLE_JUICE_SOURCE.get(), bucketProps()));

    public static final DeferredHolder<Item, BucketItem> FERMENTED_APPLE_JUICE_BUCKET = 
        ITEMS.register("fermented_apple_juice_bucket", () -> new BucketItem(ModFluids.FERMENTED_APPLE_JUICE_SOURCE.get(), bucketProps()));

    public static final DeferredHolder<Item, BucketItem> RUM_BUCKET = 
        ITEMS.register("rum_bucket", () -> new BucketItem(ModFluids.RUM_SOURCE.get(), bucketProps()));

    public static final DeferredHolder<Item, BucketItem> CIDER_BUCKET = 
        ITEMS.register("cider_bucket", () -> new BucketItem(ModFluids.CIDER_SOURCE.get(), bucketProps()));


    public static final DeferredHolder<Item, BucketItem> APPLE_WINE_BUCKET =
        ITEMS.register("apple_wine_bucket", () -> new BucketItem(ModFluids.APPLE_WINE_SOURCE.get(), bucketProps()));

    public static final DeferredHolder<Item, AlcoholBottleItem> ALCOHOL_BASE_BOTTLE =
        ITEMS.register("alcohol_base", () -> new AlcoholBottleItem(bottleProps(), () -> List.of(
            new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
            new MobEffectInstance(MobEffects.POISON, 100, 0)
        )));

    public static final DeferredHolder<Item, AlcoholBottleItem> BEER_BOTTLE =
        ITEMS.register("beer", () -> new AlcoholBottleItem(bottleProps(), () -> List.of(
            new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
            new MobEffectInstance(MobEffects.REGENERATION, 200, 0)
        )));

    public static final DeferredHolder<Item, AlcoholBottleItem> VODKA_BOTTLE =
        ITEMS.register("vodka", () -> new AlcoholBottleItem(bottleProps(), () -> List.of(
            new MobEffectInstance(MobEffects.CONFUSION, 400, 1),
            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 0),
            new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0)
        )));

    public static final DeferredHolder<Item, AlcoholBottleItem> WHISKEY_BOTTLE =
        ITEMS.register("whiskey", () -> new AlcoholBottleItem(bottleProps(), () -> List.of(
            new MobEffectInstance(MobEffects.CONFUSION, 300, 1),
            new MobEffectInstance(MobEffects.ABSORPTION, 1200, 0),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 900, 0)
        )));

    public static final DeferredHolder<Item, AlcoholBottleItem> TEQUILA_BOTTLE =
        ITEMS.register("tequila", () -> new AlcoholBottleItem(bottleProps(), () -> List.of(
            new MobEffectInstance(MobEffects.CONFUSION, 200, 2),
            new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1),
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0)
        )));

    public static final DeferredHolder<Item, AlcoholBottleItem> RUM_BOTTLE =
        ITEMS.register("rum", () -> new AlcoholBottleItem(bottleProps(), () -> List.of(
            new MobEffectInstance(MobEffects.CONFUSION, 300, 1),
            new MobEffectInstance(MobEffects.WATER_BREATHING, 1200, 0),
            new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0)
        )));

    public static final DeferredHolder<Item, AlcoholBottleItem> CIDER_BOTTLE =
        ITEMS.register("cider", () -> new AlcoholBottleItem(bottleProps(), () -> List.of(
            new MobEffectInstance(MobEffects.CONFUSION, 100, 0),
            new MobEffectInstance(MobEffects.SATURATION, 10, 1),
            new MobEffectInstance(MobEffects.HEAL, 1, 0)
        )));


    public static final DeferredHolder<Item, AlcoholBottleItem> APPLE_WINE_BOTTLE =
        ITEMS.register("apple_wine", () -> new AlcoholBottleItem(bottleProps(), () -> List.of(
            new MobEffectInstance(MobEffects.CONFUSION, 200, 0),
            new MobEffectInstance(MobEffects.REGENERATION, 300, 1),
            new MobEffectInstance(MobEffects.SATURATION, 20, 0)
        )));

    public static final DeferredHolder<Item, Item> GLASS_TUBE = 
        ITEMS.register("glass_tube", () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> TEMPERATURE_CONTROLLER = 
        ITEMS.register("temperature_controller", () -> new Item(new Item.Properties()));
    
    public static final DeferredHolder<Item, BlockItem> ALCOHOL_BOILER_ITEM =
        ITEMS.register("alcohol_boiler", () -> new BlockItem(ModBlocks.ALCOHOL_BOILER.get(), 
            new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> FERMENTATION_BARREL_ITEM =
        ITEMS.register("fermentation_barrel", () -> new BlockItem(ModBlocks.FERMENTATION_BARREL.get(), 
            new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> JUICE_PRESS_ITEM =
        ITEMS.register("juice_press", () -> new BlockItem(ModBlocks.JUICE_PRESS.get(),
            new Item.Properties()));
}
