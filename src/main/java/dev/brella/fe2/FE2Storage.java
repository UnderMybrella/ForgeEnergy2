package dev.brella.fe2;


import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Reference implementation of {@link IFE2Storage}. Use/extend this or implement your own.
 * <p>
 * Derived from the Redstone Flux power system designed by King Lemming and originally utilized in Thermal Expansion and related mods.
 * Created with consent and permission of King Lemming and Team CoFH. Released with permission under LGPL 2.1 when bundled with Forge.
 */
public class FE2Storage implements IFE2Storage, INBTSerializable<Tag> {
    protected EnergyType type;
    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public FE2Storage(EnergyType type, int capacity) {
        this(type, capacity, capacity, capacity, 0);
    }

    public FE2Storage(EnergyType type, int capacity, int maxTransfer) {
        this(type, capacity, maxTransfer, maxTransfer, 0);
    }

    public FE2Storage(EnergyType type, int capacity, int maxReceive, int maxExtract) {
        this(type, capacity, maxReceive, maxExtract, 0);
    }

    public FE2Storage(EnergyType type, int capacity, int maxReceive, int maxExtract, int energy) {
        this.type = type;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public EnergyType getEnergyType() {
        return type;
    }

    @Override
    public int receiveEnergy(EnergyType type, int maxReceive, boolean simulate) {
        if (!canReceive(type))
            return 0;

        if (this.type == type) {
            int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
            if (!simulate)
                energy += energyReceived;
            return energyReceived;
        }

        float ratio = ((float) type.getEnergyValue()) / ((float) this.type.getEnergyValue());
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, (int) Math.floor(((float) maxReceive) / ratio)));

        if (!simulate)
            energy += this.type.convertFrom(type, type.convertTo(this.type, energyReceived));

        return Math.min(maxReceive, (int) Math.ceil(((float) energyReceived) * ratio));
    }

    @Override
    public int extractEnergy(EnergyType type, int maxExtract, boolean simulate) {
        if (!canExtract(type))
            return 0;

        if (this.type == type) {
            int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
            if (!simulate)
                energy -= energyExtracted;
            return energyExtracted;
        }

        float ratio = ((float) type.getEnergyValue()) / ((float) this.type.getEnergyValue());
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, (int) Math.floor(((float) maxReceive) / ratio)));
        if (!simulate)
            energy -= this.type.convertFrom(type, type.convertTo(this.type, energyExtracted));
        return Math.min(energyExtracted, (int) Math.ceil(((float) energyExtracted) * ratio));
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract(EnergyType type) {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive(EnergyType type) {
        return this.maxReceive > 0;
    }

    @Override
    public Tag serializeNBT() {
        return IntTag.valueOf(this.getEnergyStored());
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (!(nbt instanceof IntTag intNbt))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.energy = intNbt.getAsInt();
    }
}