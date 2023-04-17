package alexthw.not_enough_glyphs.common.glyphs.propagators;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.omega;

public class PropagateSelf extends AbstractEffect implements IPropagator {

    public static final PropagateSelf INSTANCE = new PropagateSelf();

    public PropagateSelf() {
        super(omega("propagate_self"), "Propagate Self");
    }

    @Override
    public void propagate(Level world, Vec3 pos, LivingEntity shooter, SpellStats stats, SpellResolver resolver, SpellContext spellContext) {
        resolver.onResolveEffect(world, new EntityHitResult(shooter));
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return MethodSelf.INSTANCE.getCompatibleAugments();
    }

}
