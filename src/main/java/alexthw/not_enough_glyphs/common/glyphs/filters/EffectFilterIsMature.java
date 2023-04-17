package alexthw.not_enough_glyphs.common.glyphs.filters;

import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterIsMature extends EffectFilterEntity {

    public static final EffectFilterIsMature INSTANCE = new EffectFilterIsMature("filter_is_mature", "Filter: Mature");

    public EffectFilterIsMature(String tag, String description) {
        super(tag, description);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target) {
        if (!(target.getEntity() instanceof AgeableMob ageableMob)) return false;
        return !ageableMob.isBaby();
    }
}
