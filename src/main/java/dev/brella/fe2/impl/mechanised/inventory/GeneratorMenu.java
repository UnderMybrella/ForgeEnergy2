package dev.brella.fe2.impl.mechanised.inventory;

import dev.brella.fe2.impl.mechanised.Mechanisation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class GeneratorMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public GeneratorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(1), new SimpleContainerData(4));
    }

    public GeneratorMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(Mechanisation.GENERATOR_CONTAINER.get(), containerId);

        this.container = container;
        this.data = data;

        this.addSlot(new GeneratorSlot(container, 0, 56 + 24, 53 - 14));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(this.data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int fromSlot) {
        Slot slot = this.slots.get(fromSlot);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();

            if (slotStack.is(ItemTags.COALS)) {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)){
                    return ItemStack.EMPTY;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return container.stillValid(player);
    }

    public boolean isLit() {
        return data.get(0) > 0;
    }

    public int getBurnTime() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 200;
        }

        return this.data.get(0) * 13 / i;
    }

    public int getMaxBurnTime() {
        return data.get(1);
    }

    public int getEnergy() {
        return this.data.get(2);
    }

    public int getEnergyPercentage() {
        int i = this.data.get(3);
        if (i == 0) {
            i = 1_000_000;
        }

        return this.data.get(2) * 68 / i;
    }
}
