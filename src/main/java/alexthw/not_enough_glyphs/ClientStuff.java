package alexthw.not_enough_glyphs;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinderScreen;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.entity.RenderSpell;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = NotEnoughGlyphs.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ClientStuff {
    @SubscribeEvent
    public static void bindRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registry.TRAILING_PROJECTILE.get(), ClientStuff::projectileRender);
    }

    @SubscribeEvent
    public static void bindContainerRenderers(FMLClientSetupEvent event) {
        MenuScreens.register(Registry.SPELL_HOLDER.get(), SpellBinderScreen::new);
    }

    private static @NotNull EntityRenderer<EntityProjectileSpell> projectileRender(EntityRendererProvider.Context renderManager) {
        return new RenderSpell(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png"));
    }
}
