package com.mrpotato.alcohol_industry.compat.jei;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.blockentity.FermentationBarrelBlockEntity;
import com.mrpotato.alcohol_industry.registry.ModFluids;
import com.mrpotato.alcohol_industry.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FermentationCategory implements IRecipeCategory<FermentationCategory.FermentationRecipe> {

    public static final RecipeType<FermentationRecipe> RECIPE_TYPE =
        RecipeType.create(AlcoholIndustry.MOD_ID, "fermentation", FermentationRecipe.class);

    public record FermentationRecipe(FluidStack input, FluidStack output, int ticks) {
        public String timeText() {
            int seconds = ticks / 20;
            if (seconds >= 60) {
                return (seconds / 60) + "m " + (seconds % 60) + "s";
            }
            return seconds + "s";
        }
    }

    public static final List<FermentationRecipe> ALL_RECIPES = List.of(
        new FermentationRecipe(
            new FluidStack(ModFluids.APPLE_JUICE_SOURCE.get(), 1000),
            new FluidStack(ModFluids.FERMENTED_APPLE_JUICE_SOURCE.get(), 1000),
            FermentationBarrelBlockEntity.STAGE1_TICKS
        ),
        new FermentationRecipe(
            new FluidStack(ModFluids.FERMENTED_APPLE_JUICE_SOURCE.get(), 1000),
            new FluidStack(ModFluids.APPLE_WINE_SOURCE.get(), 1000),
            FermentationBarrelBlockEntity.STAGE2_TICKS
        )
    );

    private final IDrawable icon;
    private final Component title;

    public FermentationCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
            new ItemStack(ModItems.FERMENTATION_BARREL_ITEM.get()));
        this.title = Component.translatable("alcohol_industry.jei.fermentation");
    }

    @Override
    public RecipeType<FermentationRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FermentationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 4)
            .addFluidStack(recipe.input().getFluid(), recipe.input().getAmount())
            .setFluidRenderer(recipe.input().getAmount(), false, 16, 50);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 4)
            .addFluidStack(recipe.output().getFluid(), recipe.output().getAmount())
            .setFluidRenderer(recipe.output().getAmount(), false, 16, 50);
    }

    @Override
    public void draw(FermentationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, "→", 72, 20, 0xFF555555, false);

        String timeLabel = "⏱ " + recipe.timeText();
        int textWidth = font.width(timeLabel);
        guiGraphics.drawString(font, timeLabel, (160 - textWidth) / 2, 46, 0xFF666666, false);

        guiGraphics.drawString(font, recipe.input().getAmount() + "mB", 2, 56, 0xFF888888, false);
        guiGraphics.drawString(font, recipe.output().getAmount() + "mB", 128, 56, 0xFF888888, false);
    }
}
