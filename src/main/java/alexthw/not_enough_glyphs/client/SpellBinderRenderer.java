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
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;

public class SpellBinderRenderer extends GeoItemRenderer<SpellBinder> {
    public GeoModel<SpellBinder> closedModel;

    public SpellBinderRenderer() {
        super(new SpellBinderModel(SpellBinderModel.OPEN));
        this.closedModel = new SpellBinderModel(SpellBinderModel.CLOSED);
    }

    public void actuallyRender(PoseStack poseStack, SpellBinder animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int packedColor) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, packedColor);
    }

    @Override
    protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick) {
        if (this.useEntityGuiLighting) {
            Lighting.setupForEntityInInventory();
        } else {
            Lighting.setupForFlatItems();
        }
        int color = getRenderColor(animatable, partialTick, packedLight).argbInt();

        MultiBufferSource.BufferSource var10000;
        if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource2) {
            var10000 = bufferSource2;
        } else {
            var10000 = Minecraft.getInstance().renderBuffers().bufferSource();
        }
        MultiBufferSource.BufferSource defaultBufferSource = var10000;
        RenderType renderType = this.getRenderType(this.animatable, this.getTextureLocation(this.animatable), defaultBufferSource, partialTick);
        VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, this.currentItemStack != null && this.currentItemStack.hasFoil());

        poseStack.pushPose();
        this.defaultRenderGui(poseStack, this.animatable, defaultBufferSource, renderType, buffer, partialTick, packedLight, packedOverlay, color);
        defaultBufferSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        poseStack.popPose();
    }

    public void defaultRender(PoseStack poseStack, SpellBinder animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
    }

    @Override
    public void postRender(PoseStack poseStack, SpellBinder animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        var bone = model.getBone("left_cover_3");
        bone.ifPresent((b) -> {
            var mat = b.getLocalSpaceMatrix();
            poseStack.pushPose();
            poseStack.mulPose(mat);
            List<PerkItem> perks = PerkUtil.getPerksAsItems(currentItemStack);
            for (int i = 0, perksSize = perks.size(); i < perksSize; i++) {
                PerkItem perk = perks.get(i);
                poseStack.pushPose();
                poseStack.translate(0.1, i == 1 ? -0.45 : 0, -0.5);
                poseStack.rotateAround(new Quaternionf().add(0, 1, 0, 0), 0, 0, 0);
                poseStack.scale(.3f, .3f, .3f);
                Minecraft.getInstance().getItemRenderer().renderStatic(perk.getDefaultInstance(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, ArsNouveau.proxy.getClientWorld(), colour);
                poseStack.popPose();
            }

            poseStack.popPose();
                }
        );
    }

    public void defaultRenderGui(PoseStack poseStack, SpellBinder animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int packedColor) {
        poseStack.pushPose();
        BakedGeoModel model = closedModel.getBakedModel(closedModel.getModelResource(animatable));
        if (renderType == null) {
            renderType = this.getRenderType(animatable, this.getTextureLocation(animatable), bufferSource, partialTick);
        }

        if (buffer == null) {
            buffer = bufferSource.getBuffer(renderType);
        }

        this.preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, packedColor);
        if (this.firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
            this.preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, (float) packedLight, packedLight, packedOverlay);
            this.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, packedColor);
            this.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            this.postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, packedColor);
            this.firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
        }

        poseStack.popPose();
        this.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, packedColor);
    }

    public ResourceLocation getTextureLocation(SpellBinder o) {
        String base = "textures/item/spell_binder_";
        var dyeColor = currentItemStack.get(DataComponents.BASE_COLOR);
        String color = dyeColor == null ? "purple" : dyeColor.getName();
        return ResourceLocation.fromNamespaceAndPath("not_enough_glyphs", base + color + ".png");
    }

    public @NotNull RenderType getRenderType(SpellBinder animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

}
