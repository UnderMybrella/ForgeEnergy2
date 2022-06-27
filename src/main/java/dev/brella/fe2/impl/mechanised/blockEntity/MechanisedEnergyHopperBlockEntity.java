package dev.brella.fe2.impl.mechanised.blockEntity;

import dev.brella.fe2.EnergyTags;
import dev.brella.fe2.EnergyType;
import dev.brella.fe2.FE2;
import dev.brella.fe2.IFE2Storage;
import dev.brella.fe2.impl.SimpleContainerBlockEntity;
import dev.brella.fe2.impl.TaggedFE2Storage;
import dev.brella.fe2.impl.mechanised.Mechanisation;
import dev.brella.fe2.impl.mechanised.inventory.EnergyHopperMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MechanisedEnergyHopperBlockEntity extends SimpleContainerBlockEntity {
    private final EnergyType energyType;
    private final TaggedFE2Storage storage;
    private final LazyOptional<IFE2Storage> energyCapability;

    private int cooldownTime = -1;
    private long tickedGameTime;

    private final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> storage.getEnergyStored();
                case 1 -> storage.getMaxEnergyStored();
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                case 1:
                    break;
            }

        }

        public int getCount() {
            return 2;
        }
    };

    public MechanisedEnergyHopperBlockEntity(BlockPos pos, BlockState state) {
        super(Mechanisation.ENERGY_HOPPER_BLOCK_ENTITY.get(), pos, state, 0);

        this.energyType = Mechanisation.MECHANISED_OPERANDS.get();

        int value = energyType.getEnergyValue();
        this.storage = new TaggedFE2Storage(energyType, EnergyTags.INDUSTRIAL, value * 4, value);
        this.energyCapability = LazyOptional.of(() -> storage);
    }

    @Override
    public @NotNull <C> LazyOptional<C> getCapability(@NotNull Capability<C> cap, final @Nullable Direction side) {
        if (cap == FE2.CAPABILITY) return energyCapability.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.literal("Energy Hopper");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
//        return new GeneratorMenu(id, inventory, this, this.dataAccess);
        return new EnergyHopperMenu(containerId, inventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);

        compound.put("storage", storage.serializeNBT());
        compound.putInt("transfer_cooldown", this.cooldownTime);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);

        storage.deserializeNBT(compound.get("storage"));
        this.cooldownTime = compound.getInt("transfer_cooldown");
    }

    public int getAnalogOutputSignal() {
        float f = storage.getEnergyStored() / ((float) storage.getMaxEnergyStored());
        return Mth.floor(f * 14.0F) + (storage.getEnergyStored() > 0 ? 1 : 0);
    }

    public static void onServerTick(Level level, BlockPos pos, BlockState state, MechanisedEnergyHopperBlockEntity entity) {
        --entity.cooldownTime;
        entity.tickedGameTime = level.getGameTime();
        if (entity.cooldownTime <= 0) {
            entity.cooldownTime = 0;

            if (state.getValue(HopperBlock.ENABLED)) {
                if (entity.energyType.canPull()) {
                    BlockEntity blockEntity = level.getBlockEntity(pos.above());
                    if (blockEntity != null) {
                        blockEntity.getCapability(FE2.CAPABILITY, Direction.DOWN)
                                .ifPresent(storage -> {
                                    int maxTransfer = storage.extractEnergy(entity.storage.getEnergyType(), storage.getMaxEnergyStored(), true);
                                    int transferred = entity.storage.receiveEnergy(entity.storage.getEnergyType(), maxTransfer, false);
                                    storage.extractEnergy(entity.storage.getEnergyType(), transferred, false);

                                    entity.cooldownTime = 8;
                                });
                    }
                }

                if (entity.energyType.canPush()) {
                    Direction facing = state.getValue(HopperBlock.FACING);

                    BlockEntity blockEntity = level.getBlockEntity(pos.relative(facing));
                    if (blockEntity != null) {
                        blockEntity.getCapability(FE2.CAPABILITY, facing.getOpposite())
                                .ifPresent(storage -> {
                                    int maxTransfer = entity.storage.extractEnergy(entity.storage.getEnergyType(), entity.storage.getMaxEnergyStored(), true);
                                    int transferred = storage.receiveEnergy(entity.storage.getEnergyType(), maxTransfer, false);
                                    entity.storage.extractEnergy(entity.storage.getEnergyType(), transferred, false);

                                    entity.cooldownTime = 8;
                                });
                    }
                }
            }
        }
    }
}
