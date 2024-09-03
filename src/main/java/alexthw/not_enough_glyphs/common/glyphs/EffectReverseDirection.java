package alexthw.not_enough_glyphs.common.glyphs;

import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.tmg;

public class EffectReverseDirection extends AbstractEffect {
    public static final EffectReverseDirection INSTANCE = new EffectReverseDirection("reverse_direction", "Reverse Direction");

    public EffectReverseDirection(String tag, String description) {
        super(tmg(tag), description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockHitResult reversedRayTraceResult = rayTraceResult
                .withPosition(rayTraceResult.isInside()
                        ? rayTraceResult.getBlockPos()
                        : rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection()).relative(rayTraceResult.getDirection()))
                            // Relative adjustment of 2 required to get to the opposite side of the pivot block
                .withDirection(rayTraceResult.getDirection().getOpposite());
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().size())
            return;
        Spell continuation = spellContext.getRemainingSpell();
        SpellContext newContext = spellContext.clone().withSpell(continuation);
        resolver.getNewResolver(newContext).onResolveEffect(world, reversedRayTraceResult);
    }

    @Override
    public SpellTier defaultTier() { return SpellTier.ONE; }

    @Override
    public int getDefaultManaCost() { return 0; }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return Collections.emptySet();
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
