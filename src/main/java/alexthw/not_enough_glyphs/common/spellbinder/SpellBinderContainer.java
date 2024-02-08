package alexthw.not_enough_glyphs.common.spellbinder;

import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.common.items.CasterTome;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SpellBinderContainer extends AbstractContainerMenu {
    private final Container inventory;

    public SpellBinderContainer(int windowId, Inventory playerInv, ItemStack backpack) {
        this(Registry.SPELL_HOLDER.get(), windowId, playerInv, SpellBinder.getInventory(backpack));
    }

    public SpellBinderContainer(MenuType<? extends SpellBinderContainer> containerType, int windowId, Inventory playerInv, Container inventory) {
        super(containerType, windowId);
        this.inventory = inventory;
        inventory.startOpen(playerInv.player);

        // backpack inventory

        // each page have 10 slots are 5 rows of 2 slots
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 2; ++j) {
                int index = i * 2 + j;
                addSlot(this.makeSlot(inventory, -3 + i * 20, (j % 2 == 0 ? -5 : +14) + j * 20, index));
            }
        }

        // slots from 10 to 20 are in the second page, 5 rows of 2 slots
        int pageoffset = 10;
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 2; ++j) {
                int index = pageoffset + i * 2 + j;
                addSlot(this.makeSlot(inventory, -3 + i * 20, (j % 2 == 0 ? +2 : +28) + (pageoffset + j) * 12, index));
            }
        }

        // player inventory
        int offset = offset();
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInv, j1 + (l + 1) * 9, 8 + j1 * 18, offset + 84 + l * 18));
            }
        }
        // player hotbar
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInv, i1, 8 + i1 * 18, offset + 142));
        }
    }

    @NotNull
    protected Slot makeSlot(Container inventory, int y, int x, int index) {
        return new Slot(inventory, index, x, y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof SpellParchment || stack.getItem() instanceof CasterTome;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    @Override
    public void removed(Player playerIn) {
        playerIn.level().playSound(null, playerIn.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1, 1);
        super.removed(playerIn);
        this.inventory.stopOpen(playerIn);
    }

    public int offset() {
        return 50;
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return this.inventory.stillValid(playerIn);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack = slot.getItem();
            copy = itemstack.copy();
            if (index < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return copy;
    }

}
