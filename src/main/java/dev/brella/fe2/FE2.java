package dev.brella.fe2;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FE2.MODID)
public class FE2 {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "fe2";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation NAME = new ResourceLocation("energy");
    public static final ResourceKey<Registry<EnergyType>> RESOURCE_KEY = ResourceKey.createRegistryKey(NAME);
    static final DeferredRegister<EnergyType> DEFERRED_REGISTRY = DeferredRegister.create(RESOURCE_KEY, RESOURCE_KEY.location().getNamespace());
    public static final Supplier<IForgeRegistry<EnergyType>> REGISTRY = DEFERRED_REGISTRY.makeRegistry(FE2::getEnergyTypeRegistryBuilder);
    public static final Supplier<ITagManager<EnergyType>> TAGS = () -> {
        var registry = REGISTRY.get();
        return registry == null ? null : registry.tags();
    };

    public static final Capability<IFE2Storage> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public FE2() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::newRegistryEvent);
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IFE2Storage.class);
    }

    public void newRegistryEvent(NewRegistryEvent event) {
        event.create(getEnergyTypeRegistryBuilder());
    }

    public static Optional<ITag<EnergyType>> getTag(TagKey<EnergyType> key) {
        var registry = REGISTRY.get();
        if (registry == null) return Optional.empty();

        var tags = registry.tags();
        return tags == null ? Optional.empty() : Optional.of(tags.getTag(key));
    }

    public static RegistryBuilder<EnergyType> getEnergyTypeRegistryBuilder() {
        return new RegistryBuilder<EnergyType>()
                .setName(RESOURCE_KEY.location())
                .setMaxID(Integer.MAX_VALUE - 1) //Copy from GameData: We were told it is their intention to have everything in a reg be unlimited, so assume that until we find cases where it isnt.
                .hasTags()
                .disableSaving();
    }
}
