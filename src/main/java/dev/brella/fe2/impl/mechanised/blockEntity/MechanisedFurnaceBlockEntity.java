package dev.brella.fe2.impl.mechanised.blockEntity;

import dev.brella.fe2.EnergyTags;
import dev.brella.fe2.FE2;
import dev.brella.fe2.IFE2Storage;
import dev.brella.fe2.impl.TaggedFE2Storage;
import dev.brella.fe2.impl.mechanised.Mechanisation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MechanisedFurnaceBlockEntity extends BlockEntity {
    public static final int ITEM_BURN_TIME = 200;
    private final TaggedFE2Storage storage;
    private final LazyOptional<IFE2Storage> energyCapability;
    private final int burnTickCost;

    private int burnTime = ITEM_BURN_TIME;
    private int smelted = 0;

    public MechanisedFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(Mechanisation.FURNACE_BLOCK_ENTITY.get(), pos, state);

        this.storage = new TaggedFE2Storage(Mechanisation.MECHANISED_OPERANDS.get(), EnergyTags.INDUSTRIAL, 1_000_000, 25_000);
        this.energyCapability = LazyOptional.of(() -> storage);
        this.burnTickCost = (int) Math.round(((double) storage.getEnergyType().getEnergyValue()) / 1600.0 / 8.0);
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getSmelted() {
        return smelted;
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        compound.put("storage", storage.serializeNBT());
        compound.putInt("burn_time", burnTime);
        compound.putDouble("smelted", smelted);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        storage.deserializeNBT(compound.get("storage"));
        burnTime = compound.getInt("burn_time");
        smelted = compound.getInt("smelted");
    }

    @Override
    public @NotNull <C> LazyOptional<C> getCapability(@NotNull Capability<C> cap, final @Nullable Direction side) {
        if (cap == FE2.CAPABILITY) return energyCapability.cast();
        return super.getCapability(cap, side);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MechanisedFurnaceBlockEntity entity) {
        if (entity.storage.extractEnergy(entity.storage.getEnergyType(), entity.burnTickCost, true) == entity.burnTickCost) {
            entity.storage.extractEnergy(entity.storage.getEnergyType(), entity.burnTickCost, false);

            if (entity.burnTime <= 0) {
                entity.burnTime = ITEM_BURN_TIME;
                entity.smelted++;
            } else {
                entity.burnTime--;
            }

            entity.setChanged();
        } else if (entity.burnTime < ITEM_BURN_TIME){
            entity.burnTime++;

            entity.setChanged();
        }
    }
}
