package alexthw.not_enough_glyphs.common.spellbinder;

import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.items.CasterTome;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateCaster;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerCopySlot;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SpellBinderContainer extends AbstractContainerMenu {
    private final IItemHandler inventory;
    public final ItemStack binder;

    public SpellBinderContainer(int windowId, Inventory playerInv, ItemStack backpack) {
        this(Registry.SPELL_HOLDER.get(), windowId, playerInv, backpack.getCapability(Capabilities.ItemHandler.ITEM), backpack);
    }

    public SpellBinderContainer(MenuType<? extends SpellBinderContainer> containerType, int windowId, Inventory playerInv, IItemHandler inventory, ItemStack binder) {
        super(containerType, windowId);
        this.binder = binder;
        this.inventory = inventory;

        // each page have 10 slots are 5 rows of 2 slots
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 2; ++j) {
                int index = i * 2 + j;
                addSlot(this.makeSlot(inventory, -8 + i * 23, (j % 2 == 0 ? -5 : +14) + j * 20, index, this));
            }
        }

        // slots from 10 to 20 are in the second page, 5 rows of 3 slots
        int pageoffset = 10;
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 3; ++j) {
                int index = pageoffset + i * 3 + j;
                addSlot(this.makeSlot(inventory, -3 + i * 20, switch (j) {
                    default -> +2;
                    case 1 -> +9;
                    case 2 -> +16;
                } + (pageoffset + j) * 12, index, this));
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
    protected Slot makeSlot(IItemHandler inventory, int y, int x, int index, SpellBinderContainer binder) {
        return new ItemHandlerCopySlot(inventory, index, x, y) {
            @Override
            protected void setStackCopy(@NotNull ItemStack stack) {
                super.setStackCopy(stack);
                SpellBinderContainer.updateCaster(stack, binder, index);
            }

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

    private static void updateCaster(ItemStack stack, SpellBinderContainer binder, int index) {
        if (index >= 0 && index < 10) {
            AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
            AbstractCaster<?> binderCaster = SpellCasterRegistry.from(binder.binder);
            if (caster != null && binderCaster != null) {
                if (!caster.getSpell(index).recipe().equals(binderCaster.getSpell(index).recipe())) {
                    binderCaster.setSpell(caster.getSpell(), index);
                    //Networking.sendToServer(new PacketUpdateCaster(caster.getSpell(index), index, caster.getSpellName(index), true));
                }
            }
        }
    }

    @Override
    public void removed(Player playerIn) {
        playerIn.level().playSound(null, playerIn.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1, 1);
        super.removed(playerIn);
        // updates the binder spell caster with the first ten slots
        AbstractCaster<?> caster = SpellCasterRegistry.from(binder);
        if (caster == null) return;
        for (int i = 0; i < 10; i++) {
            var slotCaster = SpellCasterRegistry.from(inventory.getStackInSlot(i));
            if ((inventory.getStackInSlot(i).getItem() instanceof SpellParchment || inventory.getStackInSlot(i).getItem() instanceof CasterTome)
                    && slotCaster != null) {
                if (!caster.getSpell(i).recipe().equals(slotCaster.getSpell(i).recipe())) {
                    caster.setSpell(slotCaster.getSpell(), i);
                    Networking.sendToServer(new PacketUpdateCaster(slotCaster.getSpell(), i, slotCaster.getSpellName(), playerIn.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellBinder));
                }
            } else if (!caster.getSpell(i).isEmpty()) {
                caster.setSpell(new Spell(), i);
            }
        }
        caster.saveToStack(binder);

    }

    public int offset() {
        return 50;
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        if (playerIn.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellBinder) {
            return true;
        } else return playerIn.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof SpellBinder;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack = slot.getItem();
            copy = itemstack.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.moveItemStackTo(itemstack, this.inventory.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack, 0, this.inventory.getSlots(), false)) {
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
