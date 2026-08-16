package com.mrpotato.alcohol_industry.compat.jei;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.recipe.AlcoholBoilingRecipe;
import com.mrpotato.alcohol_industry.registry.ModItems;
import com.mrpotato.alcohol_industry.registry.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class AlcoholIndustryJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return AlcoholIndustry.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
            new AlcoholBoilingCategory(registration.getJeiHelpers().getGuiHelper()),
            new FermentationCategory(registration.getJeiHelpers().getGuiHelper()),
            new JuicePressCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<AlcoholBoilingRecipe> boilingRecipes = recipeManager
            .getAllRecipesFor(ModRecipeTypes.ALCOHOL_BOILING.getType())
            .stream()
            .map(holder -> (AlcoholBoilingRecipe) holder.value())
            .toList();
        registration.addRecipes(AlcoholBoilingCategory.RECIPE_TYPE, boilingRecipes);

        registration.addRecipes(FermentationCategory.RECIPE_TYPE, FermentationCategory.ALL_RECIPES);

        registration.addRecipes(JuicePressCategory.RECIPE_TYPE, JuicePressCategory.ALL_RECIPES);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
            new ItemStack(ModItems.ALCOHOL_BOILER_ITEM.get()),
            AlcoholBoilingCategory.RECIPE_TYPE
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModItems.FERMENTATION_BARREL_ITEM.get()),
            FermentationCategory.RECIPE_TYPE
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModItems.JUICE_PRESS_ITEM.get()),
            JuicePressCategory.RECIPE_TYPE
        );
    }
}
