package dev.brella.fe2.impl;

import dev.brella.fe2.EnergyType;
import dev.brella.fe2.FE2Storage;
import net.minecraft.tags.TagKey;

public class TaggedFE2Storage extends FE2Storage {
    protected TagKey<EnergyType> key;

    public TaggedFE2Storage(EnergyType type, TagKey<EnergyType> key, int capacity) {
        super(type, capacity);
        this.key = key;
    }

    public TaggedFE2Storage(EnergyType type, TagKey<EnergyType> key, int capacity, int maxTransfer) {
        super(type, capacity, maxTransfer);
        this.key = key;
    }

    public TaggedFE2Storage(EnergyType type, TagKey<EnergyType> key, int capacity, int maxReceive, int maxExtract) {
        super(type, capacity, maxReceive, maxExtract);
        this.key = key;
    }

    public TaggedFE2Storage(EnergyType type, TagKey<EnergyType> key, int capacity, int maxReceive, int maxExtract, int energy) {
        super(type, capacity, maxReceive, maxExtract, energy);
        this.key = key;
    }

    @Override
    public boolean canReceive(EnergyType type) {
        if (!type.is(key)) return false;

        return super.canReceive(type);
    }

    @Override
    public boolean canExtract(EnergyType type) {
        if (!type.is(key)) return false;

        return super.canExtract(type);
    }
}
