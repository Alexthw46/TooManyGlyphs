package alexthw.not_enough_glyphs.datagen;

import alexthw.not_enough_glyphs.common.glyphs.*;
import alexthw.not_enough_glyphs.common.glyphs.filters.*;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.io.IOException;
import java.nio.file.Path;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class GlyphRecipeProvider extends com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider {
    public GlyphRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        recipes.add(get(EffectPlow.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.STONE_HOE));

        recipes.add(get(MethodLayOnHands.INSTANCE).withIngredient(Ingredient.of(ItemTags.WOODEN_PRESSURE_PLATES)).withIngredient(Ingredient.of(ItemTags.BUTTONS)));
        recipes.add(get(MethodRay.INSTANCE).withItem(Items.TARGET).withItem(ItemsRegistry.SOURCE_GEM, 1));

        recipes.add(get(EffectChaining.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.CHAIN, 3).withItem(Items.LAPIS_BLOCK, 1).withItem(Items.REDSTONE_BLOCK, 1).withItem(BlockRegistry.SOURCE_GEM_BLOCK, 1));
        recipes.add(get(EffectReverseDirection.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.GLASS_PANE));

        recipes.add(get(EffectFilterBlock.INSTANCE).withIngredient(Ingredient.of(Tags.Items.COBBLESTONE)));
        recipes.add(get(EffectFilterEntity.INSTANCE).withIngredient(Ingredient.of(Tags.Items.NUGGETS_IRON)));

        recipes.add(get(EffectFilterItem.INSTANCE).withItem(Items.EMERALD));
        recipes.add(get(EffectFilterLiving.INSTANCE).withItem(Items.DANDELION));
        recipes.add(get(EffectFilterMonster.INSTANCE).withItem(Items.LILY_OF_THE_VALLEY));
        recipes.add(get(EffectFilterAnimal.INSTANCE).withItem(Items.BEEF));
        recipes.add(get(EffectFilterPlayer.INSTANCE).withItem(Items.POPPY));
        recipes.add(get(EffectFilterLivingNotMonster.INSTANCE).withItem(Items.OXEYE_DAISY));
        recipes.add(get(EffectFilterLivingNotPlayer.INSTANCE).withItem(Items.BLUE_ORCHID));

        recipes.add(get(EffectFilterIsBaby.INSTANCE).withIngredient(Ingredient.of(Tags.Items.EGGS)));
        recipes.add(get(EffectFilterIsMature.INSTANCE).withItem(Items.CHICKEN));

        Path outputBase = generator.getOutputFolder();
        for (GlyphRecipe recipe : recipes)
            DataProvider.saveStable(cache, recipe.asRecipe(), getScribeGlyphPath(outputBase, recipe.output.getItem()));
    }
    protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        return pathIn.resolve("data/" + NotEnoughGlyphs.MODID + "/recipes/" + getRegistryName(glyph).getPath() + ".json");
    }
}
