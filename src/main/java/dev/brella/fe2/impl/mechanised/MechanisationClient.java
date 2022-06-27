package dev.brella.fe2.impl.mechanised;

import dev.brella.fe2.impl.mechanised.inventory.EnergyHopperMenu;
import dev.brella.fe2.impl.mechanised.inventory.EnergyHopperScreen;
import dev.brella.fe2.impl.mechanised.inventory.GeneratorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Mechanisation.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MechanisationClient {
//    public static void registerScreens() {
//        MenuScreens.register();
//    }

    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        MenuScreens.register(Mechanisation.GENERATOR_CONTAINER.get(), GeneratorScreen::new);
        MenuScreens.register(Mechanisation.ENERGY_HOPPER_CONTAINER.get(), EnergyHopperScreen::new);
    }
}
