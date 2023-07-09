package alexthw.not_enough_glyphs.datagen;

import alexthw.not_enough_glyphs.common.glyphs.*;
import alexthw.not_enough_glyphs.common.glyphs.filters.*;
import alexthw.not_enough_glyphs.common.glyphs.propagators.PropagateOrbit;
import alexthw.not_enough_glyphs.common.glyphs.propagators.PropagateProjectile;
import alexthw.not_enough_glyphs.common.glyphs.propagators.PropagateSelf;
import alexthw.not_enough_glyphs.common.glyphs.propagators.PropagateUnderfoot;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider;
import com.hollingsworth.arsnouveau.common.spell.method.MethodOrbit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodUnderfoot;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.nio.file.Path;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class NEGGlyphRecipeProvider extends GlyphRecipeProvider {
    public NEGGlyphRecipeProvider(DataGenerator generator) {
        super(generator);
    }


    @Override
    public void collectJsons(CachedOutput pOutput) {

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

        recipes.add(get(PropagateSelf.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodSelf.INSTANCE.getGlyph()));
        recipes.add(get(PropagateProjectile.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodProjectile.INSTANCE.getGlyph()));
        recipes.add(get(PropagateOrbit.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodOrbit.INSTANCE.getGlyph()));
        recipes.add(get(PropagateUnderfoot.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodUnderfoot.INSTANCE.getGlyph()));

        Path outputBase = generator.getPackOutput().getOutputFolder();
        for (GlyphRecipe recipe : recipes)
            saveStable(pOutput, recipe.asRecipe(), getScribeGlyphPath(outputBase, recipe.output.getItem()));
    }
    protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        var regname = getRegistryName(glyph);
        return pathIn.resolve("data/" + regname.getNamespace() + "/recipes/" + regname.getPath() + ".json");
    }

}
