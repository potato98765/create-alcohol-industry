package com.mrpotato.alcohol_industry.registry;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.recipe.AlcoholBoilingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
        DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, AlcoholIndustry.MOD_ID);
    
    public static final DeferredRegister<RecipeType<?>> TYPES =
        DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, AlcoholIndustry.MOD_ID);
    
    public static final RecipeTypeInfo ALCOHOL_BOILING = 
        new RecipeTypeInfo("alcohol_boiling");
    
    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
        TYPES.register(modEventBus);
    }
    
    public static class RecipeTypeInfo implements IRecipeTypeInfo {
        private final ResourceLocation id;
        private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlcoholBoilingRecipe>> serializer;
        private final DeferredHolder<RecipeType<?>, RecipeType<AlcoholBoilingRecipe>> type;
        
        public RecipeTypeInfo(String name) {
            this.id = AlcoholIndustry.id(name);
            this.serializer = SERIALIZERS.register(name, AlcoholBoilingRecipe.Serializer::new);
            this.type = TYPES.register(name, () -> RecipeType.simple(this.id));
        }
        
        @Override
        public ResourceLocation getId() {
            return id;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) serializer.get();
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
            return (RecipeType<R>) type.get();
        }
    }
}