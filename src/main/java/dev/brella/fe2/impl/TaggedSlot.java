package dev.brella.fe2.impl;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TaggedSlot extends Slot {
    protected TagKey<Item> key;

    public TaggedSlot(TagKey<Item> key, Container container, int slot, int x, int y) {
        super(container, slot, x, y);

        this.key = key;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.is(key);
    }
}
