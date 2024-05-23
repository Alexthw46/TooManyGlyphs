package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterAny extends AbstractEffectFilter {

    public static final EffectFilterAny INSTANCE = new EffectFilterAny("filter_any", "Filter: Any");

    public EffectFilterAny(String tag, String description) {
        super(CompatRL.tmg(tag), description);
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target, Level level) {
        return true;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) { return true; }
}
