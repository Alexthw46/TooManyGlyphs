package alexthw.not_enough_glyphs.init;

import alexthw.ars_elemental.common.glyphs.MethodArcProjectile;
import alexthw.ars_elemental.common.glyphs.MethodHomingProjectile;
import alexthw.ars_elemental.common.glyphs.PropagatorArc;
import alexthw.ars_elemental.common.glyphs.PropagatorHoming;
import alexthw.not_enough_glyphs.common.glyphs.*;
import alexthw.not_enough_glyphs.common.glyphs.filters.*;
import alexthw.not_enough_glyphs.common.glyphs.propagators.*;
import alexthw.not_enough_glyphs.common.spell.FocusPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.perk.StackPerkHolder;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectReset;
import com.hollingsworth.arsnouveau.setup.registry.APIRegistry;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    static public boolean arsElemental, tooManyGlyphs, arsOmega;

    public static void registerGlyphs() {

        arsElemental = ModList.get().isLoaded("ars_elemental");
        tooManyGlyphs = ModList.get().isLoaded("toomanyglyphs");
        arsOmega = ModList.get().isLoaded("arsomega");

        //neg effects
        register(EffectPlow.INSTANCE);
        register(MethodTrail.INSTANCE);

        //tmg
        if (!tooManyGlyphs) {
            //tmg methods
            register(MethodRay.INSTANCE);

            //tmg effects
            register(EffectReverseDirection.INSTANCE);
            register(EffectChaining.INSTANCE);

            //filters
            register(EffectFilterBlock.INSTANCE);
            register(EffectFilterEntity.INSTANCE);
            register(EffectFilterLiving.INSTANCE);
            register(EffectFilterLivingNotMonster.INSTANCE);
            register(EffectFilterLivingNotPlayer.INSTANCE);
            register(EffectFilterMonster.INSTANCE);
            register(EffectFilterPlayer.INSTANCE);
            register(EffectFilterItem.INSTANCE);
            register(EffectFilterAnimal.INSTANCE);
            register(EffectFilterIsBaby.INSTANCE);
            register(EffectFilterIsMature.INSTANCE);
        }

        //neg propagators
        register(PropagatePlane.INSTANCE);

        //omega
        if (!arsOmega) {
            register(EffectFlatten.INSTANCE);

            register(PropagateUnderfoot.INSTANCE);
            register(PropagateProjectile.INSTANCE);
            register(PropagateSelf.INSTANCE);
        }
        //elemental
        if (!arsElemental) {
            register(MethodArc.INSTANCE);
            register(MethodHoming.INSTANCE);

            register(PropagateArc.INSTANCE);
            register(PropagateHoming.INSTANCE);
        } else {
            registeredSpells.addAll(List.of(
                    MethodArcProjectile.INSTANCE, MethodHomingProjectile.INSTANCE,
                    PropagatorArc.INSTANCE, PropagatorHoming.INSTANCE)
            );
            PerkRegistry.registerPerk(FocusPerk.ELEMENTAL_FIRE);
            PerkRegistry.registerPerk(FocusPerk.ELEMENTAL_WATER);
            PerkRegistry.registerPerk(FocusPerk.ELEMENTAL_EARTH);
            PerkRegistry.registerPerk(FocusPerk.ELEMENTAL_AIR);
        }

        //perks
        PerkRegistry.registerPerk(FocusPerk.MANIPULATION);
        PerkRegistry.registerPerk(FocusPerk.SUMMONING);
    }

    public static void register(AbstractSpellPart spellPart) {
        APIRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

    public static void postInit() {
        PerkRegistry.registerPerkProvider(Registry.SPELL_BINDER.get(), (s) -> new StackPerkHolder(s) {
            @Override
            public List<PerkSlot> getSlotsForTier() {
                return List.of(PerkSlot.ONE, PerkSlot.TWO);
            }
        });
        EffectReset.RESET_LIMITS.add(PropagatePlane.INSTANCE);
        EffectReset.RESET_LIMITS.add(EffectChaining.INSTANCE);
    }
}
