package alexthw.not_enough_glyphs.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
        if (Math.abs(target.getDeltaMovement().y()) <= target.getGravity()) return;

        double motionScale = 2 + 1 * Math.signum(spellStats.getAmpMultiplier()) + AMP_VALUE.get() * spellStats.getAmpMultiplier();

        Vec3 vector = target.getDeltaMovement().subtract(0, target.getGravity(), 0).normalize();
        Vec3 newSpeed = target.getDeltaMovement().add(vector.x * motionScale, vector.y * motionScale, vector.z * motionScale);

        target.setDeltaMovement(newSpeed);
        target.hurtMarked = true;

    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addAmpConfig(builder, 1.5D);
    }

    @Override
    protected int getDefaultManaCost() {
        return 100;
    }

    @Override
    protected void addAugmentCostOverrides(Map<ResourceLocation, Integer> defaults) {
        super.addAugmentCostOverrides(defaults);
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 50);
        defaults.put(AugmentDampen.INSTANCE.getRegistryName(), 50);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE);
    }

}
