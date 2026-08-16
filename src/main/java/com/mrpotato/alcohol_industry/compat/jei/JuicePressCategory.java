package com.mrpotato.alcohol_industry.compat.jei;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.blockentity.JuicePressBlockEntity;
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
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class JuicePressCategory implements IRecipeCategory<JuicePressCategory.JuicePressRecipe> {

    public static final RecipeType<JuicePressRecipe> RECIPE_TYPE =
        RecipeType.create(AlcoholIndustry.MOD_ID, "juice_pressing", JuicePressRecipe.class);

    public record JuicePressRecipe(ItemStack input, FluidStack output, int ticks) {
        public String timeText() {
            int seconds = ticks / 20;
            if (seconds >= 60) {
                return (seconds / 60) + "m " + (seconds % 60) + "s";
            }
            return seconds + "s";
        }
    }

    public static final List<JuicePressRecipe> ALL_RECIPES = List.of(
        new JuicePressRecipe(
            new ItemStack(Items.APPLE),
            new FluidStack(ModFluids.APPLE_JUICE_SOURCE.get(), JuicePressBlockEntity.JUICE_PER_ITEM),
            JuicePressBlockEntity.TICKS_PER_ITEM
        )
    );

    private final IDrawable icon;
    private final Component title;

    public JuicePressCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
            new ItemStack(ModItems.JUICE_PRESS_ITEM.get()));
        this.title = Component.translatable("alcohol_industry.jei.juice_press");
    }

    @Override
    public RecipeType<JuicePressRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, JuicePressRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 16)
            .addItemStack(recipe.input());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 4)
            .addFluidStack(recipe.output().getFluid(), recipe.output().getAmount())
            .setFluidRenderer(recipe.output().getAmount(), false, 16, 50);
    }

    @Override
    public void draw(JuicePressRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, "→", 68, 20, 0xFF555555, false);

        String timeLabel = "⏱ " + recipe.timeText();
        int textWidth = font.width(timeLabel);
        guiGraphics.drawString(font, timeLabel, (160 - textWidth) / 2, 44, 0xFF666666, false);

        guiGraphics.drawString(font, recipe.output().getAmount() + "mB", 118, 56, 0xFF888888, false);
    }
}
