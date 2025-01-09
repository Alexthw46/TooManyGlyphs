package alexthw.not_enough_glyphs.common.glyphs.effects;

import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.scalaes;

public class EffectResize extends AbstractEffect implements IPotionEffect {

    public static final EffectResize INSTANCE = new EffectResize();

    public EffectResize() {
        super(scalaes("resize"), "Resize");
    }

    @Override
    public String getName() {
        return "Resize";
    }

    @Override
    public String getBookDescription() {
        return "Resizes the target entity for a short time. Amplify to increase the size, dampen to shrink.";
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Enlarge the target.");
        map.put(AugmentDampen.INSTANCE, "Shrink the target.");
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        // the entity support the vanilla scale attribute
        if (rayTraceResult.getEntity() instanceof LivingEntity living && living.getAttribute(Attributes.SCALE) != null) {
            // if amplified, apply the expand effect, otherwise apply shrink effect
            applyConfigPotion(living, spellStats.getAmpMultiplier() >= 0 ? Registry.GROWING_EFFECT : Registry.SHRINKING_EFFECT, spellStats, false);
        }
    }

    @Override
    public void applyConfigPotion(LivingEntity entity, Holder<MobEffect> potionEffect, SpellStats spellStats, boolean particles) {
        applyPotion(entity, potionEffect, spellStats, getBaseDuration(), spellStats.getDurationMultiplier() >= 0 ? getExtendTimeDuration() : getDurationDown(), particles);
    }

    @Override
    public void applyPotion(LivingEntity entity, Holder<MobEffect> potionEffect, SpellStats stats, int baseDurationSeconds, int durationBuffSeconds, boolean showParticles) {
        if (entity == null)
            return;
        int ticks = baseDurationSeconds * 20 + durationBuffSeconds * stats.getDurationInTicks();
        int amp = (int) Math.abs(stats.getAmpMultiplier());
        entity.addEffect(new MobEffectInstance(potionEffect, ticks, amp, false, showParticles, false));
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 60 * 20);
        addExtendTimeConfig(builder, 2 * 60 * 20);
        addDurationDownConfig(builder, 20 * 20);
    }

    /**
     * The default cost generated for the config.
     * This should not be used directly for calculations, but as a helper for a recommended value.
     */
    @Override
    protected int getDefaultManaCost() {
        return 100;
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

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 60 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 120 : EXTEND_TIME.get();
    }

    public int getDurationDown() {
        return DURATION_DOWN_TIME == null ? -30 : -DURATION_DOWN_TIME.get();
    }

}

