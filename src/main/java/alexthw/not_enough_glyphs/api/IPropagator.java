package alexthw.not_enough_glyphs.api;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public interface IPropagator {

    default void copyResolver(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        spellContext.setCanceled(true);
        //keep the propagator glyph to ensure augments are applied
        Spell newSpell = spellContext.getRemainingSpell();
        newSpell.add(DUMMY, 1, 0);
        if (newSpell.isEmpty()) return;
        SpellContext newContext = spellContext.clone().withSpell(newSpell);
        SpellResolver newResolver = resolver.getNewResolver(newContext);

        propagate(world, rayTraceResult, shooter, stats, newResolver);
    }

    AbstractAugment DUMMY = new AbstractAugment("dummy", "Dummy") {
        @Override
        public int getDefaultManaCost() {
            return 0;
        }
    };

    void propagate(Level world, HitResult hitResult, LivingEntity shooter, SpellStats stats, SpellResolver resolver);

}
