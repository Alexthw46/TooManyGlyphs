package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class FilterEntity extends AbstractEffectFilter {

    public static final FilterEntity INSTANCE = new FilterEntity("filter_entity", "Filter: Entity");

    public FilterEntity(String tag, String description) {
        super(CompatRL.tmg(tag), description);
    }

    @Override
    String getDescriptionSegment() {
        return "an Entity";
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return true;
    }
}
