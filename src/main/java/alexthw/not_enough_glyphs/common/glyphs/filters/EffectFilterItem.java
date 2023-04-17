package alexthw.not_enough_glyphs.common.glyphs.filters;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterItem extends EffectFilterEntity {
    public static final EffectFilterItem INSTANCE = new EffectFilterItem("filter_item", "Filter: Item");

    public EffectFilterItem(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target) {
        return super.shouldResolveOnEntity(target) && target.getEntity() instanceof ItemEntity;
    }
}
