package alexthw.not_enough_glyphs.client;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.item.TransformAnimatedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationState;

public class SpellBinderModel extends TransformAnimatedModel<SpellBinder> {
    public static final ResourceLocation OPEN = new ResourceLocation(NotEnoughGlyphs.MODID, "geo/spell_binder.geo.json");
    public static final ResourceLocation CLOSED = new ResourceLocation(NotEnoughGlyphs.MODID, "geo/spell_binder_closed.geo.json");

    public ResourceLocation modelLoc;

    public SpellBinderModel(ResourceLocation modelLocation) {
        this.modelLoc = modelLocation;
    }


    @Override
    public void setCustomAnimations(SpellBinder
                                            entity, long uniqueID, @Nullable AnimationState<SpellBinder> customPredicate) {
        super.setCustomAnimations(entity, uniqueID, customPredicate);
    }

    @Override
    public ResourceLocation getModelResource(SpellBinder object) {
        return getModelResource(object, null);
    }

    @Override
    public ResourceLocation getModelResource(SpellBinder object, @Nullable ItemDisplayContext transformType) {
//        return modelLoc;
        if (transformType == ItemDisplayContext.GUI || transformType == ItemDisplayContext.FIXED) {
            return CLOSED;
        }
        return modelLoc;
    }


    @Override
    public ResourceLocation getTextureResource(SpellBinder object) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/item/spellbinder_purple.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpellBinder animatable) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/empty.json");
    }

    @Override
    public RenderType getRenderType(SpellBinder animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}