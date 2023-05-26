package alexthw.not_enough_glyphs.common.glyphs.propagators;

import alexthw.not_enough_glyphs.api.IPropagator;
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
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.neg;

public class PropagateOrbit extends AbstractEffect implements IPropagator {

    public static final PropagateOrbit INSTANCE = new PropagateOrbit();

    public PropagateOrbit() {
        super(neg("propagate_orbit"), "Propagate Orbit");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    public void propagate(Level world, HitResult hitResult, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            orbitOnBlock(world, shooter, resolver, stats, blockHitResult.getBlockPos(), 3 + stats.getBuffCount(AugmentSplit.INSTANCE));
        } else if (hitResult instanceof EntityHitResult entityHitResult) {
            MethodOrbit.INSTANCE.summonProjectiles(world, entityHitResult.getEntity() instanceof LivingEntity living ? living : shooter, resolver, stats);
        }
    }

    public static void orbitOnBlock(Level world, LivingEntity shooter, SpellResolver resolver, SpellStats stats, BlockPos pos, int total) {
        if (world.getBlockState(pos).isAir()) return;
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
