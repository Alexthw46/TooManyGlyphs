package io.github.derringersmods.toomanyglyphs.api;

import com.hollingsworth.arsnouveau.api.spell.*;

import java.util.List;

public class FilterUtil {
    public static IFilter getTargetFilter(SpellContext spellContext, IFilter defaultFilter) {
        return getTargetFilter(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().getSpellSize()), defaultFilter);
    }

    public static IFilter getTargetFilter(Spell spell, IFilter defaultFilter) {
        return getTargetFilter(spell.recipe, defaultFilter);
    }

    public static IFilter getTargetFilter(List<AbstractSpellPart> recipe, IFilter defaultFilter) {
        for (AbstractSpellPart part : recipe) {
            if (part instanceof IFilter) return (IFilter) part;
            if (part instanceof AbstractEffect) break;
        }
        return defaultFilter;
    }
}
