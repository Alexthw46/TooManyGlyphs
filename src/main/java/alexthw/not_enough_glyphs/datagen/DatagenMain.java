package alexthw.not_enough_glyphs.datagen;

import com.hollingsworth.arsnouveau.common.datagen.ModDatagen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class DatagenMain {

    public static CompletableFuture<HolderLookup.Provider> registries;
    public static PackOutput output;

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        ModDatagen.registries = provider;
        DataGenerator generator = event.getGenerator();

        generator.addProvider(event.includeServer(), new NEGGlyphRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new NEGApparatusProvider(generator));

        generator.addProvider(event.includeServer(), new PatchouliProvider(generator));
    }

}


