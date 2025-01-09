package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class FilterAny extends AbstractEffectFilter {

    public static final FilterAny INSTANCE = new FilterAny("filter_any", "Filter: Any");

    public FilterAny(String tag, String description) {
        super(CompatRL.tmg(tag), description);
    }

    @Override
    String getDescriptionSegment() {
        return "Placeholder";
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target, Level level) {
        return true;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) { return true; }
}
