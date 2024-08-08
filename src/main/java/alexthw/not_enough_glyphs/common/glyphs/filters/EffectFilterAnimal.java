package alexthw.not_enough_glyphs.common.glyphs.filters;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;


public class EffectFilterAnimal extends EffectFilterLiving {
    public static final EffectFilterAnimal INSTANCE = new EffectFilterAnimal("filter_animal", "Filter: Animal");

    public EffectFilterAnimal(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return super.shouldResolveOnEntity(target, level) && target.getEntity() instanceof Animal;
    }
}
