package com.mrpotato.alcohol_industry.blockentity;

import com.mrpotato.alcohol_industry.registry.ModBlockEntities;
import com.mrpotato.alcohol_industry.registry.ModFluids;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class JuicePressBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    /** mB of juice produced per item */
    public static final int JUICE_PER_ITEM = 250;
    /** Ticks per item (~1 second per item) */
    public static final int TICKS_PER_ITEM = 20;
    /** Max capacity of the output fluid tank (16 buckets = 16000 mB) */
    private static final int TANK_CAPACITY = 16000;

    private int processingTicks = 0;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(Items.APPLE);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    };

    private final FluidTank outputTank = new FluidTank(TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    public JuicePressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.JUICE_PRESS.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public FluidTank getOutputTank() {
        return outputTank;
    }

    /** Called by the block's useItemOn to insert apples */
    public ItemStack insertApples(ItemStack stack) {
        stack = inventory.insertItem(0, stack, false);
        setChanged();
        return stack;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack apples = inventory.getStackInSlot(0);
        if (apples.isEmpty()) {
            if (processingTicks != 0) {
                processingTicks = 0;
                setChanged();
            }
            return;
        }

        // Check if there's room for at least one portion of juice
        FluidStack juice = new FluidStack(ModFluids.APPLE_JUICE_SOURCE.get(), JUICE_PER_ITEM);
        int filled = outputTank.fill(juice, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
        if (filled < JUICE_PER_ITEM) {
            // Output full — pause
            if (processingTicks != 0) {
                processingTicks = 0;
                setChanged();
            }
            return;
        }

        processingTicks++;
        if (processingTicks >= TICKS_PER_ITEM) {
            // Consume one apple, produce juice
            apples.shrink(1);
            inventory.setStackInSlot(0, apples);
            outputTank.fill(juice, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
            processingTicks = 0;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void dropContents() {
        if (level == null) return;
        ItemStack apples = inventory.getStackInSlot(0);
        if (!apples.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), apples);
            inventory.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        outputTank.readFromNBT(registries, tag.getCompound("OutputTank"));
        processingTicks = tag.getInt("ProcessingTicks");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("OutputTank", outputTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("ProcessingTicks", processingTicks);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.JUICE_PRESS.get(),
            (be, side) -> be.outputTank
        );
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.JUICE_PRESS.get(),
            (be, side) -> be.inventory
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    ")
            .append(Component.translatable("alcohol_industry.goggles.juice_press")
                .withStyle(ChatFormatting.GRAY)));

        ItemStack apples = inventory.getStackInSlot(0);
        if (!apples.isEmpty()) {
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.juice_press.input")
                    .withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" " + apples.getCount() + " / 64")
                    .withStyle(ChatFormatting.WHITE)));

            if (processingTicks > 0) {
                tooltip.add(Component.literal("    ")
                    .append(Component.translatable("alcohol_industry.goggles.juice_press.pressing")
                        .withStyle(ChatFormatting.AQUA)));
            }
        } else {
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.juice_press.no_input")
                    .withStyle(ChatFormatting.DARK_GRAY)));
        }

        FluidStack juice = outputTank.getFluid();
        tooltip.add(Component.literal("    ")
            .append(Component.translatable("alcohol_industry.goggles.juice_press.output")
                .withStyle(ChatFormatting.GOLD))
            .append(Component.literal(" " + juice.getAmount() + " / " + TANK_CAPACITY + " mB")
                .withStyle(ChatFormatting.DARK_GRAY)));

        return true;
    }
}
