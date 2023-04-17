package alexthw.not_enough_glyphs.common.glyphs.filters;

import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public class EffectFilterPlayer extends EffectFilterLiving {
    public static final EffectFilterPlayer INSTANCE = new EffectFilterPlayer("filter_player", "Filter: Player");

    public EffectFilterPlayer(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target) {
        return super.shouldResolveOnEntity(target) && (target.getEntity() instanceof Player || target.getEntity() instanceof EntityDummy);
    }
}
