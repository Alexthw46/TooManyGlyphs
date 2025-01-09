package alexthw.not_enough_glyphs.common.glyphs.filters;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class FilterMonster extends FilterLiving {
    public static final FilterMonster INSTANCE = new FilterMonster("filter_monster", "Filter: Monster");

    public FilterMonster(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return super.shouldResolveOnEntity(target, level) && target.getEntity().getClassification(false) == MobCategory.MONSTER;
    }
}
