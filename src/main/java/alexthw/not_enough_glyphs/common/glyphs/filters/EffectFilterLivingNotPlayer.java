package alexthw.not_enough_glyphs.common.glyphs.filters;

import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterLivingNotPlayer extends EffectFilterLiving {
    public static final EffectFilterLivingNotPlayer INSTANCE = new EffectFilterLivingNotPlayer("filter_living_not_player", "Filter: Not Player");

    public EffectFilterLivingNotPlayer(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target) {
        return super.shouldResolveOnEntity(target) && !(target.getEntity() instanceof Player || target.getEntity() instanceof EntityDummy);
    }
}
