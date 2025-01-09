package alexthw.not_enough_glyphs.common.glyphs.propagators;

import alexthw.not_enough_glyphs.api.IPropagator;
import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import alexthw.not_enough_glyphs.common.glyphs.forms.MethodOverhead;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;

public class PropagateOverhead extends AbstractEffect implements IPropagator {

    public static final PropagateOverhead INSTANCE = new PropagateOverhead();

    public PropagateOverhead() {
        super(CompatRL.omega("propagate_overhead"), "Propagate Overhead");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public String getBookDescription() {
        return "Takes the remainder of the spell and cast it above the target.";
    }

    @Override
    public void propagate(Level world, HitResult result, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        if (result instanceof EntityHitResult entityHitResult)
            resolver.onResolveEffect(world, new BlockHitResult(entityHitResult.getEntity().getEyePosition(), Direction.DOWN, entityHitResult.getEntity().blockPosition().above(2), true));
        else if (result instanceof BlockHitResult blockHitResult)
            resolver.onResolveEffect(world, new BlockHitResult(blockHitResult.getLocation(), blockHitResult.getDirection(), blockHitResult.getBlockPos().above(), blockHitResult.isInside()));
    }

    @Override
    public Integer getTypeIndex() {
        return 8;
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.MANIPULATION);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return MethodOverhead.INSTANCE.getCompatibleAugments();
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
