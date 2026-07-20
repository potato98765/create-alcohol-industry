package com.mrpotato.alcohol_industry.block;

import com.mrpotato.alcohol_industry.blockentity.FermentationBarrelBlockEntity;
import com.mrpotato.alcohol_industry.registry.ModBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FermentationBarrelBlock extends Block implements IBE<FermentationBarrelBlockEntity>, IWrenchable, EntityBlock {
    
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    public FermentationBarrelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, 
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FermentationBarrelBlockEntity) {
            IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, hitResult.getDirection());
            if (handler != null) {
                if (FluidUtil.interactWithFluidHandler(player, hand, handler)) {
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!player.isCreative() && player.hasCorrectToolForDrops(state)) {
            Block.popResource(level, pos, new ItemStack(this));
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.FERMENTATION_BARREL.get().create(pos, state);
    }
    
    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return ModBlockEntities.FERMENTATION_BARREL.get() == type ? (BlockEntityTicker<T>) (lvl, p, st, blockEntity) -> ((FermentationBarrelBlockEntity) blockEntity).tick() : null;
    }
    
    @Override
    public Class<FermentationBarrelBlockEntity> getBlockEntityClass() {
        return FermentationBarrelBlockEntity.class;
    }
    
    @Override
    public BlockEntityType<? extends FermentationBarrelBlockEntity> getBlockEntityType() {
        return ModBlockEntities.FERMENTATION_BARREL.get();
    }
}
