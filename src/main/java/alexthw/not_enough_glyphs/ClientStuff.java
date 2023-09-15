package alexthw.not_enough_glyphs;

import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.entity.RenderSpell;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = NotEnoughGlyphs.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ClientStuff {
    @SubscribeEvent
    public static void bindRenderers(final EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(Registry.MODIFIED_ORBIT.get(), ClientStuff::projectileRender);
        event.registerEntityRenderer(Registry.TRAILING_PROJECTILE.get(), ClientStuff::projectileRender);

    }

    private static @NotNull EntityRenderer<EntityProjectileSpell> projectileRender(EntityRendererProvider.Context renderManager) {
        return new RenderSpell(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png"));
    }
}
