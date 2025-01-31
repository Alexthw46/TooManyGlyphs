package alexthw.not_enough_glyphs.common.glyphs.propagators;

import alexthw.not_enough_glyphs.api.IPropagator;
import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import alexthw.not_enough_glyphs.common.glyphs.MethodArc;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Set;

public class PropagateArc extends AbstractEffect implements IPropagator {

    public static final PropagateArc INSTANCE = new PropagateArc();

    public PropagateArc() {
        super(CompatRL.elemental("propagator_arc"), "Propagate Arc");
    }

    @Override
    public Integer getTypeIndex() {
        return 8;
    }

    @Override
    public void propagate(Level world, HitResult hitResult, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        Vec3 pos = hitResult.getLocation();
        ArrayList<EntityProjectileSpell> projectiles = new ArrayList<>();
        EntityProjectileSpell projectileSpell = new EntityProjectileSpell(world, resolver).setGravity(true);
        projectileSpell.setPos(pos.add(0, 1, 0));
        projectiles.add(projectileSpell);
        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);

        float sizeRatio = shooter.getEyeHeight() / Player.DEFAULT_EYE_HEIGHT;

        for (int i = 1; i < numSplits + 1; i++) {
            Direction offset = shooter.getDirection().getClockWise();
            if (i % 2 == 0) offset = offset.getOpposite();
            // Alternate sides
            BlockPos projPos = BlockPos.containing(pos).relative(offset, i).offset(0, (int) (1.5 * sizeRatio), 0);
            EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver).setGravity(true);
            spell.setPos(projPos.getX(), projPos.getY(), projPos.getZ());
            projectiles.add(spell);
        }

        float velocity = MethodArc.getProjectileSpeed(stats);
        Vec3 direction = pos.subtract(shooter.position());
        if (resolver.spellContext.getCaster() instanceof TileCaster tc) {
            if (tc.getTile() instanceof RotatingTurretTile rotatingTurretTile) {
                direction = rotatingTurretTile.getShootAngle();
            } else {
                direction = new Vec3(tc.getTile().getBlockState().getValue(BasicSpellTurret.FACING).step());
            }
        }
        for (EntityProjectileSpell proj : projectiles) {
            proj.setPos(proj.position().add(0, 0.25 * sizeRatio, 0));
            if (!(shooter instanceof FakePlayer)) {
                proj.shoot(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity, 0.3f);
            } else {
                proj.shoot(direction.x, direction.y, direction.z, velocity, 0.8F);
            }
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
        return MethodArc.INSTANCE.getCompatibleAugments();
    }

    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.MANIPULATION);
    }

}
