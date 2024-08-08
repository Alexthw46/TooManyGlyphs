package alexthw.not_enough_glyphs.client;


import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import javax.annotation.Nullable;
import java.util.List;

public class SpellBinderRenderer extends GeoItemRenderer<SpellBinder> {
    public GeoModel<SpellBinder> closedModel;

    public SpellBinderRenderer() {
        super(new SpellBinderModel(SpellBinderModel.OPEN));
        this.closedModel = new SpellBinderModel(SpellBinderModel.CLOSED);
    }

    public void actuallyRender(PoseStack poseStack, SpellBinder animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (this.useEntityGuiLighting) {
            Lighting.setupForEntityInInventory();
        } else {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource var10000;
        if (bufferSource instanceof MultiBufferSource.BufferSource defaultBufferSource) {
            var10000 = defaultBufferSource;
        } else {
            var10000 = Minecraft.getInstance().renderBuffers().bufferSource();
        }

        MultiBufferSource.BufferSource defaultBufferSource = var10000;
        RenderType renderType = this.getRenderType(this.animatable, this.getTextureLocation(this.animatable), defaultBufferSource, Minecraft.getInstance().getFrameTime());
        VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, this.currentItemStack != null && this.currentItemStack.hasFoil());
        poseStack.pushPose();
        this.defaultRenderGui(poseStack, this.animatable, defaultBufferSource, renderType, buffer, 0.0F, Minecraft.getInstance().getFrameTime(), packedLight, packedOverlay);
        defaultBufferSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        poseStack.popPose();
    }

    public void defaultRender(PoseStack poseStack, SpellBinder animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
    }

    @Override
    public void postRender(PoseStack poseStack, SpellBinder animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        var bone = model.getBone("left_cover_3");
        bone.ifPresent( (b) ->{
            var mat = b.getLocalSpaceMatrix();
            poseStack.pushPose();
            poseStack.mulPoseMatrix(mat);
            List<PerkItem> perks = PerkUtil.getPerksAsItems(currentItemStack);
            for (int i = 0, perksSize = perks.size(); i < perksSize; i++) {
                PerkItem perk = perks.get(i);
                poseStack.pushPose();
                poseStack.translate(0.1,i == 1 ? -0.45 : 0,-0.5);
                poseStack.rotateAround(new Quaternionf().add(0, 1, 0, 0), 0, 0, 0);
                poseStack.scale(.3f, .3f, .3f);
                Minecraft.getInstance().getItemRenderer().renderStatic(perk.getDefaultInstance(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, ArsNouveau.proxy.getClientWorld(), (int) (red + green + blue));
                poseStack.popPose();
            }

            poseStack.popPose();
                }
        );
    }

    public void defaultRenderGui(PoseStack poseStack, SpellBinder animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        Color renderColor = this.getRenderColor(animatable, partialTick, packedLight);
        float red = renderColor.getRedFloat();
        float green = renderColor.getGreenFloat();
        float blue = renderColor.getBlueFloat();
        float alpha = renderColor.getAlphaFloat();
        BakedGeoModel model = this.closedModel.getBakedModel(this.closedModel.getModelResource(animatable));
        if (renderType == null) {
            renderType = this.getRenderType(animatable, this.getTextureLocation(animatable), bufferSource, partialTick);
        }

        if (buffer == null) {
            buffer = bufferSource.getBuffer(renderType);
        }

        this.preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (this.firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
            this.preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, (float)packedLight, packedLight, packedOverlay);
            this.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            this.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            this.postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            this.firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
        }

        poseStack.popPose();
        this.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public ResourceLocation getTextureLocation(SpellBinder o) {
        String base = "textures/item/spell_binder_";
        String color = this.currentItemStack.hasTag() && this.currentItemStack.getTag().contains("color") ? DyeColor.byId(this.currentItemStack.getOrCreateTag().getInt("color")).getName() : "purple";
        return new ResourceLocation("not_enough_glyphs", base + color + ".png");
    }

    public RenderType getRenderType(SpellBinder animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

}
