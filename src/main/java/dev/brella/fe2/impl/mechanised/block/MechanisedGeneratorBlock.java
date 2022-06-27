package dev.brella.fe2.impl.mechanised.block;

import dev.brella.fe2.FE2;
import dev.brella.fe2.impl.mechanised.Mechanisation;
import dev.brella.fe2.impl.mechanised.blockEntity.MechanisedGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MechanisedGeneratorBlock extends BaseEntityBlock {
    public MechanisedGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MechanisedGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <B extends BlockEntity> BlockEntityTicker<B> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<B> type) {
        return level.isClientSide ? null : createTickerHelper(type, Mechanisation.GENERATOR_BLOCK_ENTITY.get(), MechanisedGeneratorBlockEntity::serverTick);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }
}
