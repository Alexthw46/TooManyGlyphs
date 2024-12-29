package alexthw.not_enough_glyphs.common.glyphs;

import alexthw.not_enough_glyphs.common.spell.MissileProjectile;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.omega;

public class MethodMissile extends AbstractCastMethod {

    public static final MethodMissile INSTANCE = new MethodMissile("missile", "Missile");

    public MethodMissile(String path, String description) {
        super(omega(path), description);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentPierce.INSTANCE, "Missiles will pierce through enemies and blocks an additional time.");
        map.put(AugmentSplit.INSTANCE, "Missiles multiple projectiles.");
        map.put(AugmentAccelerate.INSTANCE, "Missiles will move faster.");
        map.put(AugmentDecelerate.INSTANCE, "Missiles will move slower.");
        map.put(AugmentSensitive.INSTANCE, "Missiles will hit plants and other materials that do not block motion.");
        map.put(AugmentDurationDown.INSTANCE, "Missiles will have a shorter lifespan, exploding earlier.");
        map.put(AugmentExtendTime.INSTANCE, "Missiles will have a longer lifespan.");
        map.put(AugmentAOE.INSTANCE, "Missiles will have a larger area of effect.");
        map.put(AugmentDampen.INSTANCE, "Missiles will be affected by gravity.");
    }

    public static float getProjectileSpeed(SpellStats stats) {
        return Math.max(0.1f, 0.75f + stats.getAccMultiplier() / 2);
    }

    public ModConfigSpec.IntValue PROJECTILE_TTL;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PROJECTILE_TTL = builder.comment("Max lifespan of the projectile, in seconds.").defineInRange("max_lifespan", 60, 0, Integer.MAX_VALUE);
    }

    public void summonProjectiles(Level world, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        final boolean activate = true;
        int duration = (int) Math.max(5, 30 + 7f * stats.getDurationMultiplier());

        ArrayList<MissileProjectile> projectiles = new ArrayList<>();

        int numSplits = stats.getBuffCount(AugmentSplit.INSTANCE);
        float sizeRatio = shooter.getEyeHeight() / Player.DEFAULT_EYE_HEIGHT;
        boolean gravity = stats.hasBuff(AugmentDampen.INSTANCE);
        for (int i = 1; i < 1 + numSplits + 1; i++) {
            MissileProjectile projectileSpell = new MissileProjectile(world, resolver, duration, activate, (float) stats.getAoeMultiplier());
            projectiles.add(projectileSpell);
        }

        float velocity = getProjectileSpeed(stats);
        int opposite = -1;
        int counter = 0;
        for (MissileProjectile proj : projectiles) {
            proj.setPos(proj.position().add(0, 0.25 * sizeRatio, 0));
            proj.shoot(shooter, shooter.getXRot(), shooter.getYRot() + Math.round(counter / 2.0) * 5 * opposite, 0.0F, velocity, 0.5f);
            opposite = opposite * -1;
            counter++;
            proj.setGravity(gravity);
            world.addFreshEntity(proj);
        }
    }

    public int getDefaultManaCost() {
        return 20;
    }

    @Override
    public CastResolveType onCast(ItemStack stack, LivingEntity shooter, Level world, SpellStats stats, SpellContext context, SpellResolver resolver) {
        this.summonProjectiles(world, shooter, stats, resolver);
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        Player shooter = context.getPlayer();
        this.summonProjectiles(world, shooter, stats, resolver);
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        caster.lookAt(EntityAnchorArgument.Anchor.EYES, blockRayTraceResult.getLocation().add(0.0D, 0.0D, 0.0D));
        this.summonProjectiles(caster.getCommandSenderWorld(), caster, stats, resolver);
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnEntity(ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        this.summonProjectiles(caster.getCommandSenderWorld(), caster, stats, resolver);
        return CastResolveType.SUCCESS;
    }


    @NotNull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentAccelerate.INSTANCE, AugmentSensitive.INSTANCE, AugmentDurationDown.INSTANCE, AugmentExtendTime.INSTANCE, AugmentAOE.INSTANCE, AugmentDampen.INSTANCE);
    }

    public String getBookDescription() {
        return "Summons a projectile that applies spell effects in a small area when it hits a target or expires. Use Time augments to adjust the fuse and AoE to increase its area of effect.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.ELEMENTAL_AIR, SpellSchools.ELEMENTAL_FIRE);
    }

    @Override
    public Glyph getGlyph() {
        if (glyphItem == null) {
            glyphItem = new Glyph(this) {
                @Override
                public @NotNull String getCreatorModId(@NotNull ItemStack itemStack) {
                    return NotEnoughGlyphs.MODID;
                }
            };
        }
        return this.glyphItem;
    }
}
