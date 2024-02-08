package alexthw.not_enough_glyphs.common.spellbinder;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemInventory extends SimpleContainer {
    private final ItemStack stack;

    public ItemInventory(ItemStack stack) {
        super(20);
        this.stack = stack;

        ListTag list = stack.getOrCreateTag().getList("items", 10);

        for (int i = 0; i < 20 && i < list.size(); i++) {
            setItem(i, ItemStack.of(list.getCompound(i)));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return !stack.isEmpty();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        CompoundTag spellTag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < 20; i++) {
            if (i < 10 && getItem(i).getItem() instanceof ICasterTool c)
                spellTag.put("spell" + i, c.getSpellCaster(getItem(i)).getSpell().serialize());
            list.add(getItem(i).save(new CompoundTag()));
        }
        var tag = stack.getOrCreateTag();
        tag.put("items", list);
        var wrap = new CompoundTag();
        wrap.put("spells", spellTag);
        tag.put("ars_nouveau:caster", wrap);
    }
}