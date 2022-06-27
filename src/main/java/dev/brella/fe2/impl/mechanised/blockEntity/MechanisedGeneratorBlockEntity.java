package dev.brella.fe2.impl.mechanised.blockEntity;

import dev.brella.fe2.EnergyTags;
import dev.brella.fe2.FE2;
import dev.brella.fe2.IFE2Storage;
import dev.brella.fe2.impl.SimpleContainerBlockEntity;
import dev.brella.fe2.impl.TaggedFE2Storage;
import dev.brella.fe2.impl.mechanised.Mechanisation;
import dev.brella.fe2.impl.mechanised.block.MechanisedGeneratorBlock;
import dev.brella.fe2.impl.mechanised.inventory.GeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
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
    private int maxBurnTime = 0;
    private int burnTime = 0;
    private double buffer = 0;

    private final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> maxBurnTime;
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
                    maxBurnTime = value;
                    break;
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

        this.energyCapability = LazyOptional.of(() -> storage);
        this.perBurnTick = ((double) storage.getEnergyType().getEnergyValue()) / COAL_BURN_TIME;
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

        compound.put("storage", storage.serializeNBT());
        compound.putInt("max_burn_time", maxBurnTime);
        compound.putInt("burn_time", burnTime);
        compound.putDouble("buffer", buffer);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);

        storage.deserializeNBT(compound.get("storage"));
        maxBurnTime = compound.getInt("max_burn_time");
        burnTime = compound.getInt("burn_time");
        buffer = compound.getDouble("buffer");
    }


    public static void serverTick(Level level, BlockPos pos, BlockState state, MechanisedGeneratorBlockEntity entity) {
        boolean burning = entity.burnTime > 0;
        boolean wasBurning = burning;
        boolean changed = false;

        if (burning) {
            entity.burnTime--;
        } else if (entity.storage.getEnergyStored() < entity.storage.getMaxEnergyStored() && entity.buffer < entity.storage.getEnergyType().getEnergyValue()) {
            ItemStack stack = entity.getItem(0);
            if (!stack.isEmpty()) {
                int stackBurnTime = net.minecraftforge.common.ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
                if (stackBurnTime > 0) {
                    stack.shrink(1);
                    entity.maxBurnTime = stackBurnTime;
                    entity.burnTime = stackBurnTime;

                    burning = true;
                    changed = true;
                }
            }
        }

        if (burning) {
            entity.buffer += entity.perBurnTick;
        }

        if (entity.burnTime > 0 != wasBurning) {
            changed = true;

            state = state.setValue(MechanisedGeneratorBlock.LIT, entity.burnTime > 0);
            level.setBlock(pos, state, 3);
        }

        if (changed) {
            setChanged(level, pos, state);
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
