package com.mrpotato.alcohol_industry.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrpotato.alcohol_industry.registry.ModRecipeTypes;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class AlcoholBoilingRecipe implements Recipe<RecipeInput> {
    
    private final List<Ingredient> ingredients;
    private final FluidStack fluidIngredient;
    private final FluidStack fluidResult;
    private final HeatCondition heatRequirement;
    
    public AlcoholBoilingRecipe(List<Ingredient> ingredients, FluidStack fluidIngredient, 
                                FluidStack fluidResult, HeatCondition heatRequirement) {
        this.ingredients = ingredients;
        this.fluidIngredient = fluidIngredient;
        this.fluidResult = fluidResult;
        this.heatRequirement = heatRequirement;
    }
    
    @SuppressWarnings("deprecation")
    public boolean matches(List<ItemStack> items, FluidStack fluid) {
        if (!fluidIngredient.isEmpty()) {
            if (fluid.isEmpty() || !FluidStack.matches(fluid, fluidIngredient) || 
                fluid.getAmount() < fluidIngredient.getAmount()) {
                return false;
            }
        }
        
        List<Ingredient> remaining = new java.util.ArrayList<>(ingredients);
        for (ItemStack item : items) {
            boolean found = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (remaining.get(i).test(item)) {
                    remaining.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found && !item.isEmpty()) {
                return false;
            }
        }
        
        return remaining.isEmpty();
    }
    
    public void consumeIngredients(ItemStackHandler inventory, net.neoforged.neoforge.fluids.capability.templates.FluidTank fluidTank) {
        if (!fluidIngredient.isEmpty()) {
            fluidTank.drain(fluidIngredient.getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }
        
        List<Ingredient> remaining = new java.util.ArrayList<>(ingredients);
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                for (int j = 0; j < remaining.size(); j++) {
                    if (remaining.get(j).test(stack)) {
                        stack.shrink(1);
                        remaining.remove(j);
                        break;
                    }
                }
            }
        }
    }
    
    public FluidStack getFluidResult() {
        return fluidResult.copy();
    }
    
    public HeatCondition getRequiredHeat() {
        return heatRequirement;
    }
    
    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }
    
    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ALCOHOL_BOILING.getSerializer();
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ALCOHOL_BOILING.getType();
    }
    
    public static class Serializer implements RecipeSerializer<AlcoholBoilingRecipe> {
        
        private static final MapCodec<AlcoholBoilingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Ingredient.CODEC.listOf().fieldOf("ingredients")
                    .forGetter(r -> r.ingredients),
                FluidStack.CODEC.optionalFieldOf("fluidIngredient", FluidStack.EMPTY)
                    .forGetter(r -> r.fluidIngredient),
                FluidStack.CODEC.fieldOf("fluidResult")
                    .forGetter(r -> r.fluidResult),
                HeatCondition.CODEC.optionalFieldOf("heatRequirement", HeatCondition.NONE)
                    .forGetter(r -> r.heatRequirement)
            ).apply(instance, AlcoholBoilingRecipe::new)
        );
        
        private static final StreamCodec<RegistryFriendlyByteBuf, AlcoholBoilingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                r -> r.ingredients,
                FluidStack.STREAM_CODEC,
                r -> r.fluidIngredient,
                FluidStack.STREAM_CODEC,
                r -> r.fluidResult,
                HeatCondition.STREAM_CODEC,
                r -> r.heatRequirement,
                AlcoholBoilingRecipe::new
            );
        
        @Override
        public MapCodec<AlcoholBoilingRecipe> codec() {
            return CODEC;
        }
        
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlcoholBoilingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

