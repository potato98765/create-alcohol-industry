package com.mrpotato.alcohol_industry.blockentity;

import com.mrpotato.alcohol_industry.AlcoholIndustry;
import com.mrpotato.alcohol_industry.recipe.AlcoholBoilingRecipe;
import com.mrpotato.alcohol_industry.registry.ModBlockEntities;
import com.mrpotato.alcohol_industry.registry.ModRecipeTypes;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class AlcoholBoilerBlockEntity extends SmartBlockEntity {

    private final ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
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

    private final FluidTank inputTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private final FluidTank outputTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private int processingTicks;
    private static final int MAX_TICKS = 300;
    private RecipeHolder<AlcoholBoilingRecipe> currentRecipe;

    public AlcoholBoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALCOHOL_BOILER.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
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
        
        if (level.getGameTime() % 100 == 0) {
        }

        if (allRecipes.isEmpty()) {
            currentRecipe = null;
            return;
        }

        int recipeIndex = 0;
        for (RecipeHolder<?> holder : allRecipes) {
            recipeIndex++;
            
            if (holder.value() instanceof AlcoholBoilingRecipe recipe) {
                if (level.getGameTime() % 100 == 0) {
                }
                
                boolean matches = recipe.matches(items, fluid);
                boolean canOutput = canOutput(recipe);
                
                if (level.getGameTime() % 100 == 0) {
                }
                
                if (matches && canOutput) {
                    if (currentRecipe == null) {
                        currentRecipe = (RecipeHolder<AlcoholBoilingRecipe>) holder;
                    }
                    return;
                }
            }
        }

        if (currentRecipe != null) {
        }
        currentRecipe = null;
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
                    if (side == Direction.UP)
                        return be.outputTank;
                    if (side != Direction.DOWN)
                        return be.inputTank;
                    return null;
                }
        );
    }
}
