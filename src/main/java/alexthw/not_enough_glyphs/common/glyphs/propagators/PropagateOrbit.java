package alexthw.not_enough_glyphs.common.glyphs.propagators;

import alexthw.not_enough_glyphs.common.spell.ModifiedOrbitProjectile;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodOrbit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.omega;

public class PropagateOrbit extends AbstractEffect implements IPropagator {

    public static final PropagateOrbit INSTANCE = new PropagateOrbit();

    public PropagateOrbit() {
        super(omega("propagate_orbit"), "Propagate Orbit");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void copyResolver(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        spellContext.setCanceled(true);
        Spell newSpell = spellContext.getRemainingSpell();
        if (newSpell.isEmpty()) return;
        SpellContext newContext = spellContext.clone().withSpell(newSpell);
        SpellResolver newResolver = resolver.getNewResolver(newContext);
        propagate(world, shooter, newResolver, stats, rayTraceResult);
    }

    @Override
    public void propagate(Level world, Vec3 pos, LivingEntity shooter, SpellStats stats, SpellResolver resolver, SpellContext spellContext) {

    }

    private void propagate(Level world, LivingEntity shooter, SpellResolver resolver, SpellStats stats, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            orbitOnBlock(world, shooter, resolver, stats, blockHitResult.getBlockPos(), 3 + stats.getBuffCount(AugmentSplit.INSTANCE));
        } else {
            MethodOrbit.INSTANCE.summonProjectiles(world, hitResult instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity living
                    ? living : shooter, resolver, stats);
        }
    }

    public static void orbitOnBlock(Level world, LivingEntity shooter, SpellResolver resolver, SpellStats stats, BlockPos pos, int total) {
        for (int i = 0; i < total; i++) {
            ModifiedOrbitProjectile wardProjectile = new ModifiedOrbitProjectile(world, resolver);

            wardProjectile.setCenter(pos);
            wardProjectile.setOwnerID(shooter.getId());
            wardProjectile.setOffset(i);
            wardProjectile.setAccelerates((int) stats.getAccMultiplier());
            wardProjectile.setAoe((float) stats.getAoeMultiplier());
            wardProjectile.extendTimes = (int) stats.getDurationMultiplier();
            wardProjectile.setTotal(total);
            wardProjectile.setColor(resolver.spellContext.getColors());
            world.addFreshEntity(wardProjectile);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 300;
    }

    @Override
    @NotNull
    public Set<AbstractAugment> getCompatibleAugments() {
        return MethodOrbit.INSTANCE.getCompatibleAugments();
    }

    @Override
    public Integer getTypeIndex() {
        return 8;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

}
