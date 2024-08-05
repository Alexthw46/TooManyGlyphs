package alexthw.not_enough_glyphs.common.glyphs.filters;

import alexthw.not_enough_glyphs.common.glyphs.CompatRL;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterLiving extends AbstractEffectFilter {

    public static final EffectFilterLiving INSTANCE = new EffectFilterLiving("filter_living", "Filter: Living");

    public EffectFilterLiving(String tag, String description) {
        super(CompatRL.tmg(tag), description);
    }

    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        if (!(target.getEntity() instanceof LivingEntity)) return false;
        return target.getEntity().isAlive();
    }
}
