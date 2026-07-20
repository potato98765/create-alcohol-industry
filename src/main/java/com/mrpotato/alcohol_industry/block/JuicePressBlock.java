package com.mrpotato.alcohol_industry.block;

import com.mrpotato.alcohol_industry.blockentity.JuicePressBlockEntity;
import com.mrpotato.alcohol_industry.registry.ModBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import org.jetbrains.annotations.Nullable;

public class JuicePressBlock extends Block implements IBE<JuicePressBlockEntity>, IWrenchable, EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public JuicePressBlock(Properties properties) {
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
        if (be instanceof JuicePressBlockEntity press) {
            // Try fluid extraction first (bucket etc.)
            var fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, hitResult.getDirection());
            if (fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
                return ItemInteractionResult.SUCCESS;
            }

            // Insert fruit items
            if (stack.is(Items.APPLE)) {
                ItemStack remainder = press.insertApples(stack.copy());
                if (remainder.getCount() < stack.getCount()) {
                    if (!player.isCreative()) {
                        stack.setCount(remainder.getCount());
                    }
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
        // Intentionally NOT calling super — Create's IBE routes through loot tables
        // which would cause duplicate or missing drops for SmartBlockEntity blocks.
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof JuicePressBlockEntity press) {
                press.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.JUICE_PRESS.get().create(pos, state);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return ModBlockEntities.JUICE_PRESS.get() == type
            ? (BlockEntityTicker<T>) (lvl, p, st, blockEntity) -> ((JuicePressBlockEntity) blockEntity).tick()
            : null;
    }

    @Override
    public Class<JuicePressBlockEntity> getBlockEntityClass() {
        return JuicePressBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends JuicePressBlockEntity> getBlockEntityType() {
        return ModBlockEntities.JUICE_PRESS.get();
    }
}
