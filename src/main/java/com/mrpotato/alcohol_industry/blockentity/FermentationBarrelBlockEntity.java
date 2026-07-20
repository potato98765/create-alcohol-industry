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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class FermentationBarrelBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    /** Stage 1: Apple Juice -> Fermented Apple Juice (~2 min at 20 tps) */
    public static final int STAGE1_TICKS = 2400;
    /** Stage 2: Fermented Apple Juice -> Apple Wine (~5 min at 20 tps) */
    public static final int STAGE2_TICKS = 6000;

    private final FluidTank tank = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    private int processingTicks = 0;

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTATION_BARREL.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public FluidTank getTank() {
        return tank;
    }

    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }

        FluidStack fluid = tank.getFluid();

        // Stage 1: Apple Juice -> Fermented Apple Juice
        if (!fluid.isEmpty() && fluid.getFluid() == ModFluids.APPLE_JUICE_SOURCE.get() && fluid.getAmount() >= 100) {
            processingTicks++;
            if (processingTicks % 20 == 0) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            if (processingTicks >= STAGE1_TICKS) {
                int currentAmount = fluid.getAmount();
                tank.setFluid(new FluidStack(ModFluids.FERMENTED_APPLE_JUICE_SOURCE.get(), currentAmount));
                processingTicks = 0;
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }

        // Stage 2: Fermented Apple Juice -> Apple Wine
        } else if (!fluid.isEmpty() && fluid.getFluid() == ModFluids.FERMENTED_APPLE_JUICE_SOURCE.get() && fluid.getAmount() >= 100) {
            processingTicks++;
            if (processingTicks % 20 == 0) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            if (processingTicks >= STAGE2_TICKS) {
                int currentAmount = fluid.getAmount();
                tank.setFluid(new FluidStack(ModFluids.APPLE_WINE_SOURCE.get(), currentAmount));
                processingTicks = 0;
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }

        } else {
            if (processingTicks != 0) {
                processingTicks = 0;
                setChanged();
            }
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        tank.readFromNBT(registries, tag.getCompound("Tank"));
        processingTicks = tag.getInt("ProcessingTicks");
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.put("Tank", tank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("ProcessingTicks", processingTicks);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.FERMENTATION_BARREL.get(),
                (be, side) -> be.tank
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    ")
            .append(Component.translatable("alcohol_industry.goggles.fermentation_barrel")
                .withStyle(ChatFormatting.GRAY)));

        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.fermentation_barrel.contents")
                    .withStyle(ChatFormatting.GOLD)));
            tooltip.add(Component.literal("      ")
                .append(fluid.getHoverName().copy().withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": " + fluid.getAmount() + " / " + tank.getCapacity() + " mB")
                    .withStyle(ChatFormatting.DARK_GRAY)));

            if (fluid.getFluid() == ModFluids.APPLE_JUICE_SOURCE.get()) {
                int percent = (int) (((double) processingTicks / STAGE1_TICKS) * 100.0);
                tooltip.add(Component.literal("    ")
                    .append(Component.translatable("alcohol_industry.goggles.fermentation_barrel.fermenting")
                        .withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" " + percent + "%")
                        .withStyle(ChatFormatting.WHITE)));
                tooltip.add(Component.literal("      ")
                    .append(Component.translatable("alcohol_industry.goggles.fermentation_barrel.stage1")
                        .withStyle(ChatFormatting.GRAY)));
            } else if (fluid.getFluid() == ModFluids.FERMENTED_APPLE_JUICE_SOURCE.get()) {
                int percent = (int) (((double) processingTicks / STAGE2_TICKS) * 100.0);
                tooltip.add(Component.literal("    ")
                    .append(Component.translatable("alcohol_industry.goggles.fermentation_barrel.fermenting")
                        .withStyle(ChatFormatting.DARK_RED))
                    .append(Component.literal(" " + percent + "%")
                        .withStyle(ChatFormatting.WHITE)));
                tooltip.add(Component.literal("      ")
                    .append(Component.translatable("alcohol_industry.goggles.fermentation_barrel.stage2")
                        .withStyle(ChatFormatting.GRAY)));
            }
        } else {
            tooltip.add(Component.literal("    ")
                .append(Component.translatable("alcohol_industry.goggles.fermentation_barrel.empty")
                    .withStyle(ChatFormatting.DARK_GRAY)));
        }

        return true;
    }
}
