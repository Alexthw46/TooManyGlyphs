package alexthw.not_enough_glyphs.api;

import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.not_enough_glyphs.common.spell.FocusPerk;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static alexthw.not_enough_glyphs.common.spellbinder.SpellBinder.getHolderForPerkHands;

public class ElementalCompat {

    public static boolean focusCheck(ItemStack stack, LivingEntity unwrappedCaster) {
        if (stack.getItem() instanceof ISchoolFocus focus) {
            SpellSchool school = focus.getSchool();
            Perk perkToCheck = null;
            if (school == SpellSchools.ELEMENTAL_FIRE) perkToCheck = FocusPerk.ELEMENTAL_FIRE;
            if (school == SpellSchools.ELEMENTAL_WATER) perkToCheck = FocusPerk.ELEMENTAL_WATER;
            if (school == SpellSchools.ELEMENTAL_EARTH) perkToCheck = FocusPerk.ELEMENTAL_EARTH;
            if (school == SpellSchools.ELEMENTAL_AIR) perkToCheck = FocusPerk.ELEMENTAL_AIR;
            if (perkToCheck != null) return getHolderForPerkHands(perkToCheck, unwrappedCaster) != null;
        }
        return false;
    }

}
