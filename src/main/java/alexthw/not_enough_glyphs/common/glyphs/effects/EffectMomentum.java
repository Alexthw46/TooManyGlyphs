package alexthw.not_enough_glyphs.common.glyphs.effects;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectMomentum extends AbstractEffect {

    public static final EffectMomentum INSTANCE = new EffectMomentum();

    public EffectMomentum() {
        super(CompatRL.neg("momentum"), "Momentum");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        Entity target = rayTraceResult.getEntity();
        if (target.isPassenger())
            target = target.getRootVehicle();
        Vec3 currentMovement = target.getDeltaMovement();

        // Get the X and Z components (horizontal movement) and normalize them
        double horizontalMagnitude = Math.sqrt(currentMovement.x * currentMovement.x + currentMovement.z * currentMovement.z);
        //avoid division by zero
        double normalizedX = horizontalMagnitude == 0 ? 0 : currentMovement.x / horizontalMagnitude;
        double normalizedZ = horizontalMagnitude == 0 ? 0 : currentMovement.z / horizontalMagnitude;

        // Set the desired leap speed
        double horizontalBoost = 1.5 + AMP_VALUE.get() * spellStats.getAmpMultiplier();

        double gravity = target.getGravity();
        double verticalAmp = 1 + AMP_VALUE.get() * spellStats.getAmpMultiplier();
        // defy gravity if the entity is moving upwards, otherwise increase towards gravity
        double verticalBoost;
        if (target instanceof Projectile || target.isNoGravity()) {
            verticalBoost = 0;
        } else if (Math.round(currentMovement.y * 100.0) / 100.0 >= -gravity) {
            verticalBoost = gravity + 0.5 * verticalAmp;
        } else verticalBoost = -0.5 * verticalAmp;

        Vec3 momentum = new Vec3(
                normalizedX * horizontalBoost,  // X direction boost
                verticalBoost,                  // Y direction boost
                normalizedZ * horizontalBoost   // Z direction boost
        );

        // Add leaping momentum to the entity movement vector
        target.setDeltaMovement(currentMovement.add(momentum));
        target.hurtMarked = true;

    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addAmpConfig(builder, 1.5D);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 10);
    }

    @Override
    protected int getDefaultManaCost() {
        return 100;
    }

    @Override
    protected void addAugmentCostOverrides(Map<ResourceLocation, Integer> defaults) {
        super.addAugmentCostOverrides(defaults);
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 50);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 3);
    }


    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Increases the speed multiplier applied to the target.");
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentAmplify.INSTANCE);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }
}
