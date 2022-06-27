package dev.brella.fe2.impl;

import dev.brella.fe2.EnergyType;
import dev.brella.fe2.FE2Storage;
import net.minecraft.tags.TagKey;

public class TaggedFE2GeneratorStorage extends TaggedFE2Storage {
    public TaggedFE2GeneratorStorage(EnergyType type, TagKey<EnergyType> key, int capacity) {
        super(type, key, capacity);
    }

    public TaggedFE2GeneratorStorage(EnergyType type, TagKey<EnergyType> key, int capacity, int maxTransfer) {
        super(type, key, capacity, maxTransfer);
    }

    public TaggedFE2GeneratorStorage(EnergyType type, TagKey<EnergyType> key, int capacity, int maxReceive, int maxExtract) {
        super(type, key, capacity, maxReceive, maxExtract);
    }

    public TaggedFE2GeneratorStorage(EnergyType type, TagKey<EnergyType> key, int capacity, int maxReceive, int maxExtract, int energy) {
        super(type, key, capacity, maxReceive, maxExtract, energy);
    }

    public int generate(int maxGenerated, boolean simulate) {
        int energyGenerated = Math.min(capacity - energy, Math.min(this.maxReceive, maxGenerated));
        if (!simulate)
            energy += energyGenerated;
        return energyGenerated;
    }

    @Override
    public boolean canReceive(EnergyType type) {
        return false;
    }

    @Override
    public boolean canExtract(EnergyType type) {
        if (!type.is(key)) return false;

        return super.canExtract(type);
    }
}
