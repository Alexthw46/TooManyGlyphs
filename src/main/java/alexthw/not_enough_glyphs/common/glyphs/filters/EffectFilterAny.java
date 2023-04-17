package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterAny extends AbstractEffectFilter {

    public static final EffectFilterAny INSTANCE = new EffectFilterAny("filter_any", "Filter: Any");

    public EffectFilterAny(String tag, String description) {
        super(CompatRL.tmg(tag), description);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target) {
        return true;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target) { return true; }
}
