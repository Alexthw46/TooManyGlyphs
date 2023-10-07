package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class EffectFilterBlock extends AbstractEffectFilter implements IFilter {

    public static final EffectFilterBlock INSTANCE = new EffectFilterBlock("filter_block", "Filter: Block");

    public EffectFilterBlock(String tag, String description) {
        super(CompatRL.tmg(tag), description);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target, Level level) {
        return true;
    }
}
