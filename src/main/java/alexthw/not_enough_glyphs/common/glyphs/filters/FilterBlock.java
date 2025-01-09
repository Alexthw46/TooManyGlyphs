package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import com.hollingsworth.arsnouveau.api.spell.IFilter;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class FilterBlock extends AbstractEffectFilter implements IFilter {

    public static final FilterBlock INSTANCE = new FilterBlock("filter_block", "Filter: Block");

    public FilterBlock(String tag, String description) {
        super(CompatRL.tmg(tag), description);
    }

    @Override
    String getDescriptionSegment() {
        return "a Block";
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
