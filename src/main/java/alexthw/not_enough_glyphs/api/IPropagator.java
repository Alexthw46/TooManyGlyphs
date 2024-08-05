package alexthw.not_enough_glyphs.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public interface IPropagator {

    default void copyResolver(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        SpellContext newContext = spellContext.makeChildContext();
        newContext.withSpell(newContext.getSpell().mutable().add(0, DUMMY).immutable());
        SpellResolver newResolver = resolver.getNewResolver(newContext);
        spellContext.setCanceled(true);
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
