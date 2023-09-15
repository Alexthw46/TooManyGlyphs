package alexthw.not_enough_glyphs.init;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NotEnoughGlyphs.MODID)
public class NotEnoughGlyphs
{

    public static final String MODID = "not_enough_glyphs";

    public NotEnoughGlyphs() {
        if (!FMLEnvironment.production) ArsNouveauAPI.ENABLE_DEBUG_NUMBERS = true;

        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.init(modbus);
        ArsNouveauRegistry.registerGlyphs();
        modbus.addListener(this::setup);
        modbus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Networking::registerNetwork);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

}
