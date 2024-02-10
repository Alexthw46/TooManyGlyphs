package alexthw.not_enough_glyphs.datagen;

import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.nio.file.Path;

public class NEGApparatusProvider extends ApparatusRecipeProvider {
    public NEGApparatusProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    static TagKey<Item> SPELLBOOKS = TagKey.create(Registries.ITEM, new ResourceLocation("ars_nouveau", "spellbook"));
    @Override
    public void collectJsons(CachedOutput pOutput) {
        recipes.add(builder().withReagent(Ingredient.of(SPELLBOOKS)).withPedestalItem(8,Items.LEATHER).withResult(Registry.SPELL_BINDER.get()).build());

        for (var recipe : recipes) {
            Path path = getRecipePath(this.output, recipe.getId().getPath());
            this.saveStable(pOutput, recipe.asRecipe(), path);
        }
    }
}
