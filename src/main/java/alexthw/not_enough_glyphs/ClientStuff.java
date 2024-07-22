package alexthw.not_enough_glyphs;

import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.client.renderer.entity.RenderSpell;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class ClientStuff {
    @SubscribeEvent
    public static void bindRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registry.TRAILING_PROJECTILE.get(), ClientStuff::projectileRender);
    }

    @SubscribeEvent
    public static void bindContainerRenderers(FMLClientSetupEvent event) {
        //MenuScreens.register(Registry.SPELL_HOLDER.get(), SpellBinderScreen::new);
    }

    private static @NotNull EntityRenderer<EntityProjectileSpell> projectileRender(EntityRendererProvider.Context renderManager) {
        return new RenderSpell(renderManager, prefix("textures/entity/spell_proj.png"));
    }
}
