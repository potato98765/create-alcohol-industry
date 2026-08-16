package com.mrpotato.alcohol_industry.blockentity;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.recipe.AlcoholBoilingRecipe;
import com.mrpotato.alcohol_industry.registry.ModBlockEntities;
import com.mrpotato.alcohol_industry.registry.ModRecipeTypes;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class AlcoholBoilerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private final ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (level == null) return true;
            var recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ALCOHOL_BOILING.getType());
            for (RecipeHolder<?> holder : recipes) {
                if (holder.value() instanceof AlcoholBoilingRecipe recipe) {
                    for (net.minecraft.world.item.crafting.Ingredient ingredient : recipe.getIngredients_()) {
                        if (ingredient.test(stack)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    };

    public ItemStack insertItem(ItemStack stack) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            stack = inventory.insertItem(i, stack, false);
            if (stack.isEmpty())
                break;
        }
        setChanged();
        return stack;
    }

    private final FluidTank inputTank = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    private final FluidTank outputTank = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    private int processingTicks;
    private static final int MAX_TICKS = 200;
    private RecipeHolder<AlcoholBoilingRecipe> currentRecipe;

    public AlcoholBoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALCOHOL_BOILER.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }


    public FluidTank getInputTank() {
        return inputTank;
    }

    public FluidTank getOutputTank() {
        return outputTank;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public int getProcessingTicks() {
        return processingTicks;
    }

    public int getMaxProcessingTicks() {
        return MAX_TICKS;
    }

    public boolean isProcessing() {
        return processingTicks > 0;
    }

    public float getProcessingProgress() {
        if (MAX_TICKS == 0) return 0f;
        return (float) processingTicks / MAX_TICKS;
    }


    @Override
    public void tick() {
        if (level == null || level.isClientSide)
            return;

        if (level.getGameTime() % 20 == 0) {
            findRecipe();
        }

        if (currentRecipe == null) {
            processingTicks = 0;
            return;
        }

        HeatCondition currentHeat = getHeatCondition();
        HeatCondition requiredHeat = currentRecipe.value().getRequiredHeat();
        
        if (currentHeat.ordinal() < requiredHeat.ordinal()) {
            processingTicks = 0;
            return;
        }

        processingTicks++;

        if (processingTicks % 10 == 0) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }

        if (processingTicks >= MAX_TICKS) {
            finishRecipe();
            processingTicks = 0;
            currentRecipe = null;
        }
    }

    private void findRecipe() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }

        FluidStack fluid = inputTank.getFluid();

        var allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ALCOHOL_BOILING.getType());

        if (allRecipes.isEmpty()) {
            if (currentRecipe != null) {
                currentRecipe = null;
                processingTicks = 0;
            }
            return;
        }

        if (currentRecipe != null) {
            if (currentRecipe.value().matches(items, fluid) && canOutput(currentRecipe.value())) {
                return;
            }
        }

        for (RecipeHolder<?> holder : allRecipes) {
            if (holder.value() instanceof AlcoholBoilingRecipe recipe) {
                if (recipe.matches(items, fluid) && canOutput(recipe)) {
                    currentRecipe = (RecipeHolder<AlcoholBoilingRecipe>) holder;
                    processingTicks = 0;
                    return;
                }
            }
        }

        if (currentRecipe != null) {
            currentRecipe = null;
            processingTicks = 0;
        }
    }

    private boolean canOutput(AlcoholBoilingRecipe recipe) {
        FluidStack out = recipe.getFluidResult();
        FluidStack cur = outputTank.getFluid();

        return cur.isEmpty()
                || (FluidStack.isSameFluidSameComponents(cur, out)
                && cur.getAmount() + out.getAmount() <= outputTank.getCapacity());
    }

    private void finishRecipe() {
        AlcoholBoilingRecipe recipe = currentRecipe.value();
        recipe.consumeIngredients(inventory, inputTank);
        outputTank.fill(recipe.getFluidResult(), IFluidHandler.FluidAction.EXECUTE);
        
        if (level != null) {
            level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, 
                SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        
        setChanged();
    }

    private HeatCondition getHeatCondition() {
        if (level == null)
            return HeatCondition.NONE;

        BlockState state = level.getBlockState(worldPosition.below());
        Block block = state.getBlock();

        if (block instanceof BlazeBurnerBlock) {
            BlazeBurnerBlock.HeatLevel heatLevel = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            return heatLevel == BlazeBurnerBlock.HeatLevel.SEETHING
                    ? HeatCondition.SUPERHEATED
                    : HeatCondition.HEATED;
        }

        return HeatCondition.NONE;
    }

    public void dropContents() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(
                        level,
                        worldPosition.getX(),
                        worldPosition.getY(),
                        worldPosition.getZ(),
                        stack
                );
            }
        }
    }


    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        inputTank.readFromNBT(registries, tag.getCompound("InputTank"));
        outputTank.readFromNBT(registries, tag.getCompound("OutputTank"));
        processingTicks = tag.getInt("ProcessingTicks");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("InputTank", inputTank.writeToNBT(registries, new CompoundTag()));
        tag.put("OutputTank", outputTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("ProcessingTicks", processingTicks);
    }


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ALCOHOL_BOILER.get(),
                (be, side) -> side != Direction.DOWN ? be.inventory : null
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.ALCOHOL_BOILER.get(),
                (be, side) -> {
                    if (side == Direction.UP || side == null)
                        return be.outputTank;
                    if (side != Direction.DOWN)
                        return be.inputTank;
                    return null;
                }
        );
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    ")
            .append(Component.translatable("alcohol_industry.goggles.alcohol_boiler")
                .withStyle(ChatFormatting.GRAY)));

        FluidStack inputFluid = inputTank.getFluid();
        if (!inputFluid.isEmpty()) {
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.alcohol_boiler.input")
                    .withStyle(ChatFormatting.GOLD)));
            tooltip.add(Component.literal("      ")
                .append(inputFluid.getHoverName().copy().withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": " + inputFluid.getAmount() + " / " + inputTank.getCapacity() + " mB")
                    .withStyle(ChatFormatting.DARK_GRAY)));
        } else {
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.alcohol_boiler.input_empty")
                    .withStyle(ChatFormatting.DARK_GRAY)));
        }

        FluidStack outputFluid = outputTank.getFluid();
        if (!outputFluid.isEmpty()) {
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.alcohol_boiler.output")
                    .withStyle(ChatFormatting.GREEN)));
            tooltip.add(Component.literal("      ")
                .append(outputFluid.getHoverName().copy().withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": " + outputFluid.getAmount() + " / " + outputTank.getCapacity() + " mB")
                    .withStyle(ChatFormatting.DARK_GRAY)));
        } else {
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.alcohol_boiler.output_empty")
                    .withStyle(ChatFormatting.DARK_GRAY)));
        }

        if (isProcessing()) {
            int percent = (int) (getProcessingProgress() * 100);
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.alcohol_boiler.processing")
                    .withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" " + percent + "%")
                    .withStyle(ChatFormatting.WHITE)));
        }

        HeatCondition heat = getHeatCondition();
        ChatFormatting heatColor = switch (heat) {
            case SUPERHEATED -> ChatFormatting.LIGHT_PURPLE;
            case HEATED -> ChatFormatting.YELLOW;
            default -> ChatFormatting.DARK_GRAY;
        };
        tooltip.add(Component.literal("    ")
            .append(Component.translatable("alcohol_industry.goggles.alcohol_boiler.heat")
                .withStyle(heatColor))
            .append(Component.literal(" " + heat.getSerializedName())
                .withStyle(heatColor)));

        return true;
    }
}
