package alexthw.not_enough_glyphs.common.glyphs.filters;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterItem extends EffectFilterEntity {
    public static final EffectFilterItem INSTANCE = new EffectFilterItem("filter_item", "Filter: Item");

    public EffectFilterItem(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return super.shouldResolveOnEntity(target, level) && target.getEntity() instanceof ItemEntity;
    }
}
