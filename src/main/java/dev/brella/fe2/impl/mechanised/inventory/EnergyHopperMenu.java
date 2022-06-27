package dev.brella.fe2.impl.mechanised.inventory;

import dev.brella.fe2.impl.ForgeFurnaceFuelSlot;
import dev.brella.fe2.impl.mechanised.Mechanisation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class EnergyHopperMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public EnergyHopperMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(0), new SimpleContainerData(2));
    }

    public EnergyHopperMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(Mechanisation.ENERGY_HOPPER_CONTAINER.get(), containerId);

        this.container = container;
        this.data = data;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 51 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 109));
        }

        this.addDataSlots(this.data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int fromSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return container.stillValid(player);
    }

    public int getEnergy() {
        return this.data.get(0);
    }

    public int getEnergyPercentage() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 1_000_000;
        }

        return this.data.get(0) * 68 / i;
    }
}
