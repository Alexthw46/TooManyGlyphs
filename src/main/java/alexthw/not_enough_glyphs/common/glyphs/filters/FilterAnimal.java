package alexthw.not_enough_glyphs.common.glyphs.filters;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class FilterAnimal extends FilterLiving {
    public static final FilterAnimal INSTANCE = new FilterAnimal("filter_animal", "Filter: Animal");

    public FilterAnimal(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return super.shouldResolveOnEntity(target, level) && target.getEntity() instanceof Animal;
    }

    @Override
    String getDescriptionSegment() {
        return "an Animal";
    }
}
