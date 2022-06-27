package dev.brella.fe2.impl.mechanised.blockEntity;

import dev.brella.fe2.EnergyTags;
import dev.brella.fe2.FE2;
import dev.brella.fe2.IFE2Storage;
import dev.brella.fe2.impl.SimpleContainerBlockEntity;
import dev.brella.fe2.impl.TaggedFE2Storage;
import dev.brella.fe2.impl.mechanised.Mechanisation;
import dev.brella.fe2.impl.mechanised.inventory.GeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MechanisedGeneratorBlockEntity extends SimpleContainerBlockEntity {
    private static final int COAL_BURN_TIME = 1600;

    private final TaggedFE2Storage storage;
    private final LazyOptional<IFE2Storage> energyCapability;
    private final double perBurnTick;
    private int burnTime = 0;
    private double buffer = 0;

    private final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> COAL_BURN_TIME;
                case 2 -> storage.getEnergyStored();
                case 3 -> storage.getMaxEnergyStored();
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                    burnTime = value;
                    break;
                case 1:
                case 2:
                case 3:
                    break;
            }

        }

        public int getCount() {
            return 4;
        }
    };

    public MechanisedGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(Mechanisation.GENERATOR_BLOCK_ENTITY.get(), pos, state, 1);

        this.storage = new TaggedFE2Storage(Mechanisation.MECHANISED_OPERANDS.get(), EnergyTags.INDUSTRIAL, 1_000_000, 25_000);
        ;
        this.energyCapability = LazyOptional.of(() -> storage);
        this.perBurnTick = ((double) storage.getEnergyType().getUnitsForCoal()) / COAL_BURN_TIME;
    }

    @Override
    public @NotNull <C> LazyOptional<C> getCapability(@NotNull Capability<C> cap, final @Nullable Direction side) {
        if (cap == FE2.CAPABILITY) return energyCapability.cast();
        return super.getCapability(cap, side);
    }

    public int getBurnTime() {
        return burnTime;
    }

    public double getBuffer() {
        return buffer;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);

        ContainerHelper.saveAllItems(compound, this.items);

        compound.put("storage", storage.serializeNBT());
        compound.putInt("burn_time", burnTime);
        compound.putDouble("buffer", buffer);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);

        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.items);

        storage.deserializeNBT(compound.get("storage"));
        burnTime = compound.getInt("burn_time");
        buffer = compound.getDouble("buffer");
    }


    public static void serverTick(Level level, BlockPos pos, BlockState state, MechanisedGeneratorBlockEntity entity) {
        // Generate 1 coal's worth of energy

        boolean burning = entity.burnTime > 0;
        if (burning) {
            entity.burnTime--;
            entity.buffer += entity.perBurnTick;
        } else if (entity.storage.getEnergyStored() < entity.storage.getMaxEnergyStored()) {
            ItemStack stack = entity.getItem(0);
            if (!stack.isEmpty() && stack.is(ItemTags.COALS)) {
                stack.shrink(1);
                entity.burnTime = COAL_BURN_TIME;

                burning = true;
            }
        }

        if (burning) {
            entity.buffer += entity.perBurnTick;
        }

        // Transfer from buffer
        if (entity.buffer > 0)
            entity.buffer -= entity.storage.receiveEnergy(entity.storage.getEnergyType(), (int) entity.buffer, false);

        //Transfer to adjacent
        for (Direction dir : Direction.values()) {
            var blockEntity = level.getBlockEntity(pos.offset(dir.getStepX(), dir.getStepY(), dir.getStepZ()));
            if (blockEntity != null) {
                blockEntity.getCapability(FE2.CAPABILITY, dir.getOpposite())
                        .ifPresent((storage) -> {
                            var extracted = entity.storage.extractEnergy(
                                    entity.storage.getEnergyType(),
                                    entity.storage.getMaxEnergyStored(),
                                    true);

                            var received = storage.receiveEnergy(
                                    entity.storage.getEnergyType(),
                                    extracted,
                                    false);

                            entity.storage.extractEnergy(
                                    entity.storage.getEnergyType(),
                                    received,
                                    false);
                        });
            }
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.literal("Generator");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        return new GeneratorMenu(id, inventory, this, this.dataAccess);
    }
}
