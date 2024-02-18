package alexthw.not_enough_glyphs.common.spell;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FocusPerk extends Perk {
    public static final FocusPerk MANIPULATION = new FocusPerk(new ResourceLocation(ArsNouveau.MODID, "thread_shaper_focus"));
    public static final FocusPerk SUMMONING = new FocusPerk(new ResourceLocation(ArsNouveau.MODID, "thread_summon_focus"));

    public FocusPerk(ResourceLocation key) {
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
