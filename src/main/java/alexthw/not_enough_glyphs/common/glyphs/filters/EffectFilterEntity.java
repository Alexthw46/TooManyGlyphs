package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterEntity extends AbstractEffectFilter {

    public static final EffectFilterEntity INSTANCE = new EffectFilterEntity("filter_entity", "Filter: Entity");

    public EffectFilterEntity(String tag, String description) {
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
