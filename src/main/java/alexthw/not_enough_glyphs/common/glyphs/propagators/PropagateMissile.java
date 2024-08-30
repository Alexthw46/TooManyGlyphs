package alexthw.not_enough_glyphs.common.glyphs.propagators;

import alexthw.not_enough_glyphs.api.IPropagator;
import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import alexthw.not_enough_glyphs.common.glyphs.MethodMissile;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Set;

public class PropagateMissile extends AbstractEffect implements IPropagator {

    public static final PropagateMissile INSTANCE = new PropagateMissile();

    public PropagateMissile() {
        super(CompatRL.omega("propagate_missile"), "Propagate Missile");
    }

    @Override
    public Integer getTypeIndex() {
        return 8;
    }

    @Override
    public void propagate(Level world, HitResult hitResult, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        Vec3 pos = hitResult.getLocation();
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList<>();
        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

        int opposite = -1;
        int counter = 0;

        for (int i = 0; i < numSplits + 1; i++) {
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
            projectiles.add(spell);
        }

        float velocity = MethodMissile.getProjectileSpeed(stats);
        boolean gravity = stats.hasBuff(AugmentDampen.INSTANCE);
        Vec3 direction = pos.subtract(shooter.position());
        if (resolver.spellContext.getCaster() instanceof TileCaster tc) {
            if (tc.getTile() instanceof RotatingTurretTile rotatingTurretTile) {
                direction = rotatingTurretTile.getShootAngle();
            } else {
                direction = new Vec3(tc.getTile().getBlockState().getValue(BasicSpellTurret.FACING).step());
            }
        }
        for (EntityProjectileSpell proj : projectiles) {
            proj.setPos(pos.add(0, 1, 0));
            if (!(shooter instanceof FakePlayer)) {
                proj.shoot(shooter, shooter.getXRot(), shooter.getYRot() + Math.round(counter / 2.0) * 5 * opposite, 0.0F, velocity, 0.8f);
            } else {
                proj.shoot(direction.x, direction.y, direction.z, velocity, 0.8F);
            }
            opposite = opposite * -1;
            counter++;
            world.addFreshEntity(proj);
            proj.setGravity(gravity);
            world.addFreshEntity(proj);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public int getDefaultManaCost() {
        return 200;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return MethodMissile.INSTANCE.getCompatibleAugments();
    }

    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.MANIPULATION);
    }
}
