package com.mrpotato.alcohol_industry.block;

import com.mrpotato.alcohol_industry.blockentity.AlcoholBoilerBlockEntity;
import com.mrpotato.alcohol_industry.registry.ModBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

public class AlcoholBoilerBlock extends Block implements IBE<AlcoholBoilerBlockEntity>, IWrenchable, EntityBlock {
    
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    private static final VoxelShape SHAPE;
    static {
        VoxelShape body = Block.box(1, 0, 1, 15, 14, 15);
        VoxelShape rim = Block.box(0, 14, 0, 16, 16, 16);
        SHAPE = Shapes.join(body, rim, BooleanOp.OR);
    }
    
    public AlcoholBoilerBlock(Properties properties) {
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
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, 
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        
        AlcoholBoilerBlockEntity be = getBlockEntity(level, pos);
        if (be != null) {
            var fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, hitResult.getDirection());
            if (fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
                return ItemInteractionResult.SUCCESS;
            }

            if (!stack.isEmpty()) {
                ItemStack remainder = be.insertItem(stack.copy());
                if (remainder.getCount() < stack.getCount()) {
                    if (!player.isCreative()) {
                        stack.shrink(stack.getCount() - remainder.getCount());
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
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            AlcoholBoilerBlockEntity be = getBlockEntity(level, pos);
            if (be != null) {
                be.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AlcoholBoilerBlockEntity boiler && boiler.isProcessing()) {
            double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            
            level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 
                0.0, 0.04 + random.nextDouble() * 0.02, 0.0);
            
            if (random.nextInt(4) == 0) {
                level.addParticle(ParticleTypes.BUBBLE_POP, x, y - 0.2, z, 
                    0.0, 0.02, 0.0);
            }
            
            if (random.nextInt(12) == 0) {
                level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS,
                    0.3F, 0.8F + random.nextFloat() * 0.4F, false);
            }
        }
    }
    
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.ALCOHOL_BOILER.get().create(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        
        return createTickerHelper(type, ModBlockEntities.ALCOHOL_BOILER.get(), 
            (lvl, pos, st, blockEntity) -> blockEntity.tick());
    }
    
    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
    
    
    @Override
    public Class<AlcoholBoilerBlockEntity> getBlockEntityClass() {
        return AlcoholBoilerBlockEntity.class;
    }
    
    @Override
    public BlockEntityType<? extends AlcoholBoilerBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ALCOHOL_BOILER.get();
    }
}
