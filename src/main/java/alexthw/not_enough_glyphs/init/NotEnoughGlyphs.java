package alexthw.not_enough_glyphs.init;

import alexthw.not_enough_glyphs.ClientStuff;
import alexthw.not_enough_glyphs.Events;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(NotEnoughGlyphs.MODID)
public class NotEnoughGlyphs
{

    public static final String MODID = "not_enough_glyphs";

    public NotEnoughGlyphs(IEventBus modEventBus, ModContainer modContainer) {
        if (!FMLEnvironment.production) ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = true;
        Registry.init(modEventBus);
        Events.registerListeners(modEventBus, NeoForge.EVENT_BUS);
        ArsNouveauRegistry.registerGlyphs();
        modEventBus.addListener(Networking::register);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        //NeoForge.EVENT_BUS.register(this);
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.register(ClientStuff.class);
        }
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ArsNouveauRegistry::postInit);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

}
