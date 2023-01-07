package io.github.derringersmods.toomanyglyphs.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.IFilter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterBlockOrLiving extends AbstractEffectFilter implements IFilter {

    public static final EffectFilterBlockOrLiving INSTANCE = new EffectFilterBlockOrLiving("filter_block", "Filter: Block");

    public EffectFilterBlockOrLiving(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target) {
        if (!(target.getEntity() instanceof LivingEntity living)) return false;
        return living.isAlive();
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target) {
        return true;
    }
}
