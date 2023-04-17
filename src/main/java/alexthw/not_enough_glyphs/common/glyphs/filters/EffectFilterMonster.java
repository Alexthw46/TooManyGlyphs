package alexthw.not_enough_glyphs.common.glyphs.filters;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterMonster extends EffectFilterLiving {
    public static final EffectFilterMonster INSTANCE = new EffectFilterMonster("filter_monster", "Filter: Monster");

    public EffectFilterMonster(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target) {
        return super.shouldResolveOnEntity(target) && target.getEntity().getClassification(false) == MobCategory.MONSTER;
    }
}
