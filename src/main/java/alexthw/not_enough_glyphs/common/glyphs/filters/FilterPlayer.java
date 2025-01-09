package alexthw.not_enough_glyphs.common.glyphs.filters;

import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class FilterPlayer extends FilterLiving {
    public static final FilterPlayer INSTANCE = new FilterPlayer("filter_player", "Filter: Player");

    public FilterPlayer(String tag, String description) {
        super(tag, description);
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level level) {
        return super.shouldResolveOnEntity(target, level) && (target.getEntity() instanceof Player || target.getEntity() instanceof EntityDummy);
    }
}
