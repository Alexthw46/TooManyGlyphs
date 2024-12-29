package alexthw.not_enough_glyphs.common.glyphs.filters;

import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;


public class EffectFilterIsBaby extends EffectFilterEntity {

    public static final EffectFilterIsBaby INSTANCE = new EffectFilterIsBaby("filter_is_baby", "Filter: Baby");

    public EffectFilterIsBaby(String tag, String description) {
        super(tag, description);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        if (!(target.getEntity() instanceof AgeableMob ageableMob)) return false;
        return ageableMob.isBaby();
    }

    @Override
    String getDescriptionSegment() {
        return "a baby";
    }

}
