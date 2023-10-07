package alexthw.not_enough_glyphs.common.mixin;


import alexthw.not_enough_glyphs.api.FilterUtil;
import alexthw.not_enough_glyphs.common.glyphs.filters.EffectFilterAny;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(RuneBlock.class)
public class RuneMixin {
    @Redirect(method = "entityInside",
            at = @At(value = "FIELD",
                    target = "Lcom/hollingsworth/arsnouveau/common/block/tile/RuneTile;touchedEntity:Lnet/minecraft/world/entity/Entity;",
                    remap = false,
                    opcode = Opcodes.PUTFIELD))
    private void entityInsideFilterCheck(@Nonnull RuneTile instance, Entity value) {
        if (instance.touchedEntity != null) return;
        if (!FilterUtil.getTargetFilter(instance.spell, EffectFilterAny.INSTANCE).shouldResolveOnEntity(value, value.level()))
            return;
        instance.touchedEntity = value;
    }

}
