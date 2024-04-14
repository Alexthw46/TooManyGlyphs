package alexthw.not_enough_glyphs.common.spell;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BookPerk extends Perk {

    public BookPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getName() {
        return Component.translatable(getRegistryName().getNamespace() + ".book_thread", Component.translatable("item." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()).getString()).getString();
    }

    @Override
    public boolean validForSlot(PerkSlot slot, ItemStack stack, Player player) {
        return super.validForSlot(slot, stack, player) && stack.getItem() instanceof SpellBinder;
    }

}
