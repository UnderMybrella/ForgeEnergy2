package dev.brella.fe2.impl.mechanised;

import dev.brella.fe2.EnergyType;
import dev.brella.fe2.FE2;
import dev.brella.fe2.impl.mechanised.block.MechanisedFurnaceBlock;
import dev.brella.fe2.impl.mechanised.block.MechanisedGeneratorBlock;
import dev.brella.fe2.impl.mechanised.blockEntity.MechanisedFurnaceBlockEntity;
import dev.brella.fe2.impl.mechanised.blockEntity.MechanisedGeneratorBlockEntity;
import dev.brella.fe2.impl.mechanised.inventory.GeneratorMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

@Mod(Mechanisation.ID)
public class Mechanisation {
    public static final String ID = "mechanisation";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ID);
    public static final DeferredRegister<EnergyType> ENERGY = DeferredRegister.create(FE2.RESOURCE_KEY, ID);

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ID);

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab("mechanisation") {
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(Items.REDSTONE);
        }
    };

    public static final RegistryObject<Block> GENERATOR_BLOCK = BLOCKS.register("generator", () -> new MechanisedGeneratorBlock(BlockBehaviour.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> FURNACE_BLOCK = BLOCKS.register("furnace", () -> new MechanisedFurnaceBlock(BlockBehaviour.Properties.of(Material.STONE)));

    public static final RegistryObject<Item> GENERATOR_BLOCK_ITEM = ITEMS.register("generator", () -> new BlockItem(GENERATOR_BLOCK.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistryObject<Item> FURNACE_BLOCK_ITEM = ITEMS.register("furnace", () -> new BlockItem(FURNACE_BLOCK.get(), new Item.Properties().tab(CREATIVE_TAB)));

    public static final RegistryObject<BlockEntityType<MechanisedGeneratorBlockEntity>> GENERATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register("generator", () -> BlockEntityType.Builder.of(MechanisedGeneratorBlockEntity::new, GENERATOR_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<MechanisedFurnaceBlockEntity>> FURNACE_BLOCK_ENTITY = BLOCK_ENTITIES.register("furnace", () -> BlockEntityType.Builder.of(MechanisedFurnaceBlockEntity::new, FURNACE_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<GeneratorMenu>> GENERATOR_CONTAINER = CONTAINERS.register("generator", () -> new MenuType<>(GeneratorMenu::new));

    public static final RegistryObject<EnergyType> MECHANISED_OPERANDS = ENERGY.register("mechanised_operands",
            () -> new EnergyType(EnergyType.Properties.create()
                    .convertFrom(0.8f)
                    .convertTo(0.7f)
                    .unitsForCoal(128_000)));

    public Mechanisation() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ENERGY.register(modEventBus);
        CONTAINERS.register(modEventBus);
    }
}
