package alexthw.not_enough_glyphs.datagen;

import alexthw.not_enough_glyphs.common.spell.FocusPerk;
import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeProvider;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
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
        addRecipe(builder().withReagent(Ingredient.of(SPELLBOOKS)).withPedestalItem(8,Items.LEATHER).withResult(Registry.SPELL_BINDER.get()).build());
        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(ItemsRegistry.SHAPERS_FOCUS).withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE).withResult(getPerkItem(FocusPerk.MANIPULATION.getRegistryName())).build());
        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(ItemsRegistry.SUMMONING_FOCUS).withPedestalItem(2, ItemsRegistry.CONJURATION_ESSENCE).withResult(getPerkItem(FocusPerk.SUMMONING.getRegistryName())).build());

        for (var recipe : recipes) {
            Path path = getRecipePath(this.output, recipe.getId().getPath());
            this.saveStable(pOutput, recipe.asRecipe(), path);
        }
    }
}
