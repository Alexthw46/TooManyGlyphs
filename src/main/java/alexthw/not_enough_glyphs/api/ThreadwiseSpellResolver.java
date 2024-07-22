package alexthw.not_enough_glyphs.api;

import alexthw.not_enough_glyphs.common.spell.FocusPerk;
import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import alexthw.not_enough_glyphs.init.ArsNouveauRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ThreadwiseSpellResolver extends SpellResolver {

    public ThreadwiseSpellResolver(SpellContext spellContext) {
        super(spellContext);
    }

    @Override
    public boolean hasFocus(ItemStack stack) {
        if (super.hasFocus(stack)) return true;
//        if (stack.getItem() == ItemsRegistry.SHAPERS_FOCUS.get())
//            return SpellBinder.getHolderForPerkHands(FocusPerk.MANIPULATION, this.spellContext.getUnwrappedCaster()) != null;
//        if (stack.getItem() == ItemsRegistry.SUMMONING_FOCUS.get())
//            return SpellBinder.getHolderForPerkHands(FocusPerk.SUMMONING, this.spellContext.getUnwrappedCaster()) != null;
        if (ArsNouveauRegistry.arsElemental)
            return ElementalCompat.focusCheck(stack, this.spellContext.getUnwrappedCaster());
        return false;
    }

    @Override
    public boolean hasFocus(Item stack) {
        return hasFocus(stack.getDefaultInstance());
    }

    @Override
    public SpellResolver getNewResolver(SpellContext context) {
        SpellResolver newResolver = new ThreadwiseSpellResolver(context);
        newResolver.previousResolver = this;
        return newResolver;
    }

}
