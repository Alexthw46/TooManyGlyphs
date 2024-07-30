package alexthw.not_enough_glyphs.common.spellbinder;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//
//public class SpellItemInventory extends SimpleContainer {
//    private final ItemStack stack;
//
//    public SpellItemInventory(ItemStack stack) {
//        super(25);
//        this.stack = stack;
//
//        ListTag list = stack.set   ().getList("items", 10);
//
//        for (int i = 0; i < 25 && i < list.size(); i++) {
//            setItem(i, ItemStack.of(list.getCompound(i)));
//        }
//    }
//
//    @Override
//    public boolean stillValid(@NotNull Player player) {
//        return !stack.isEmpty();
//    }
//
//    @Override
//    public void setChanged() {
//        super.setChanged();
//        List<Spell> spells = NonNullList.withSize(10, new Spell());
//        ListTag list = new ListTag();
//        for (int i = 0; i < 25; i++) {
//            if (i < 10 && getItem(i).getItem() instanceof ICasterTool c)
//                spells.set(i, c.getSpellCaster(getItem(i)).getSpell());
//            list.add(getItem(i).save(new CompoundTag()));
//        }
//        var tag = stack.getOrCreateTag();
//        tag.put("items", list);
//
//        if (stack.getItem() instanceof ICasterTool c) {
//            for (int i = 0; i < spells.size(); i++) {
//                c.getSpellCaster(stack).setSpell(spells.get(i), i);
//            }
//        }
//    }
//}