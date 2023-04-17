package alexthw.not_enough_glyphs.common.glyphs.propagators;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.omega;

public class PropagateProjectile extends AbstractEffect implements IPropagator {
    public static final PropagateProjectile INSTANCE = new PropagateProjectile();

    public PropagateProjectile() {
        super(omega("propagate_projectile"), "Propagate Projectile");
    }

    public void propagate(Level world, Vec3 pos, LivingEntity shooter, SpellStats stats, SpellResolver resolver, SpellContext spellContext) {
        int numSplits = 1 + stats.getBuffCount(AugmentSplit.INSTANCE);

        List<EntityProjectileSpell> projectiles = new ArrayList<>();
        for (int i = 0; i < numSplits; i++) {
            projectiles.add(new EntityProjectileSpell(world, resolver));
        }
        float velocity = Math.max(0.1f, 0.75f + stats.getAccMultiplier() / 2);
        int opposite = -1;
        int counter = 0;

        Vec3 direction = pos.subtract(shooter.position());
        if (spellContext.castingTile instanceof BasicSpellTurretTile turretTile) {
            if (turretTile instanceof RotatingTurretTile rotatingTurretTile) {
                direction = rotatingTurretTile.getShootAngle();
            } else {
                direction = new Vec3(turretTile.getBlockState().getValue(BasicSpellTurret.FACING).step());
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
        }

    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public int getDefaultManaCost() {
        return 400;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return MethodProjectile.INSTANCE.getCompatibleAugments();
    }

    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public Integer getTypeIndex() {
        return 8;
    }

}