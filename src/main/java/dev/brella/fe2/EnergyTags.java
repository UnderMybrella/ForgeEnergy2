package dev.brella.fe2;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public final class EnergyTags {
    public static final TagKey<EnergyType> INDUSTRIAL = bind("forge:industrial");
    public static final TagKey<EnergyType> MAGICAL = bind("forge:magical");

    private EnergyTags() {
    }

    private static TagKey<EnergyType> bind(String name) {
        return TagKey.create(FE2.RESOURCE_KEY, new ResourceLocation(name));
    }

    public static TagKey<EnergyType> create(final ResourceLocation name) {
        return TagKey.create(FE2.RESOURCE_KEY, name);
    }
}
