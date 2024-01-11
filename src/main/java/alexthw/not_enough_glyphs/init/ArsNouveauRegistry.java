package alexthw.not_enough_glyphs.init;

import alexthw.not_enough_glyphs.common.glyphs.*;
import alexthw.not_enough_glyphs.common.glyphs.filters.*;
import alexthw.not_enough_glyphs.common.glyphs.propagators.*;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectReset;
import com.hollingsworth.arsnouveau.setup.registry.APIRegistry;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void registerGlyphs() {

        //neg effects
        register(EffectPlow.INSTANCE);
        register(MethodTrail.INSTANCE);

        //tmg
        if (!ModList.get().isLoaded("toomanyglyphs")) {
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
        //register(PropagateOrbit.INSTANCE);
        register(PropagatePlane.INSTANCE);

        //omega
        if (!ModList.get().isLoaded("arsomega")) {
            register(EffectFlatten.INSTANCE);

            register(PropagateUnderfoot.INSTANCE);
            register(PropagateProjectile.INSTANCE);
            register(PropagateSelf.INSTANCE);
        }
        //elemental
        if (!ModList.get().isLoaded("ars_elemental")) {
            register(MethodArc.INSTANCE);
            register(MethodHoming.INSTANCE);

            register(PropagateArc.INSTANCE);
            register(PropagateHoming.INSTANCE);
        }
    }

    public static void register(AbstractSpellPart spellPart) {
        APIRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

    public static void postInit() {
        EffectReset.RESET_LIMITS.add(PropagatePlane.INSTANCE);
        EffectReset.RESET_LIMITS.add(EffectChaining.INSTANCE);
    }
}
