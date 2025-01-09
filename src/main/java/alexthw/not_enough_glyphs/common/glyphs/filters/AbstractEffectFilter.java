package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public abstract class AbstractEffectFilter extends AbstractFilter {

    public AbstractEffectFilter(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public String getBookDescription() {
        return "Stops the spell from resolving unless it targets " + getDescriptionSegment();
    }

    abstract String getDescriptionSegment();

    public AbstractEffectFilter inverted() {
        this.inverted = !inverted;
        return this;
    }

    @Override
    public boolean shouldAffect(HitResult rayTraceResult, Level level) {
        return inverted != super.shouldAffect(rayTraceResult, level);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!shouldAffect(rayTraceResult, world)) spellContext.setCanceled(true);
    }

    protected boolean inverted = false;

    @Override
    public Integer getTypeIndex() {
        return 15;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return Collections.emptySet();
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target, Level level) {
        return false;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return false;
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
