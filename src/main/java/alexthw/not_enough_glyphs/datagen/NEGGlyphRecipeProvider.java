package alexthw.not_enough_glyphs.datagen;

import alexthw.ars_elemental.common.glyphs.MethodArcProjectile;
import alexthw.ars_elemental.common.glyphs.MethodHomingProjectile;
import alexthw.ars_elemental.common.glyphs.PropagatorArc;
import alexthw.ars_elemental.common.glyphs.PropagatorHoming;
import alexthw.not_enough_glyphs.common.glyphs.effects.*;
import alexthw.not_enough_glyphs.common.glyphs.filters.*;
import alexthw.not_enough_glyphs.common.glyphs.forms.*;
import alexthw.not_enough_glyphs.common.glyphs.propagators.*;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.datagen.GlyphRecipeProvider;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodUnderfoot;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.nio.file.Path;

import static alexthw.not_enough_glyphs.init.ArsNouveauRegistry.arsElemental;
import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class NEGGlyphRecipeProvider extends GlyphRecipeProvider {
    public NEGGlyphRecipeProvider(DataGenerator generator) {
        super(generator);
    }


    @Override
    public void collectJsons(CachedOutput pOutput) {

        recipes.add(get(EffectPlow.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.STONE_HOE));
        recipes.add(get(EffectFlatten.INSTANCE).withItem(ItemsRegistry.EARTH_ESSENCE).withItem(Items.IRON_SHOVEL).withItem(Items.ANVIL));

        addRecipe(arsElemental ? MethodArcProjectile.INSTANCE : MethodArc.INSTANCE, Items.ARROW, Items.SNOWBALL, Items.SLIME_BALL, Items.ENDER_PEARL);
        addRecipe(arsElemental ? MethodHomingProjectile.INSTANCE : MethodHoming.INSTANCE, Items.NETHER_STAR, ItemsRegistry.MANIPULATION_ESSENCE, ItemsRegistry.DOWSING_ROD, Items.ENDER_EYE);

        addRecipe(arsElemental ? PropagatorArc.INSTANCE : PropagateArc.INSTANCE, ItemsRegistry.MANIPULATION_ESSENCE, (arsElemental ? MethodArcProjectile.INSTANCE : MethodArc.INSTANCE).getGlyph());
        addRecipe(arsElemental ? PropagatorHoming.INSTANCE : PropagateHoming.INSTANCE, ItemsRegistry.MANIPULATION_ESSENCE, (arsElemental ? MethodHomingProjectile.INSTANCE : MethodHoming.INSTANCE).getGlyph());

//        addRecipe(MethodArc.INSTANCE, Items.ARROW, Items.SNOWBALL, Items.SLIME_BALL, Items.ENDER_PEARL);
//        addRecipe(MethodHoming.INSTANCE, Items.NETHER_STAR, ItemsRegistry.MANIPULATION_ESSENCE, ItemsRegistry.DOWSING_ROD, Items.ENDER_EYE);
//
//        addRecipe(PropagateArc.INSTANCE, ItemsRegistry.MANIPULATION_ESSENCE, MethodArc.INSTANCE.getGlyph());
//        addRecipe(PropagateHoming.INSTANCE, ItemsRegistry.MANIPULATION_ESSENCE, MethodHoming.INSTANCE.getGlyph());

        recipes.add(get(MethodRay.INSTANCE).withItem(Items.TARGET).withItem(ItemsRegistry.SOURCE_GEM, 1));
        recipes.add(get(MethodTrail.INSTANCE).withItem(Items.DRAGON_BREATH).withItem(Items.ECHO_SHARD, 2).withItem(ItemsRegistry.AIR_ESSENCE));
        recipes.add(get(EffectChaining.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.CHAIN, 3).withItem(Items.LAPIS_BLOCK, 1).withItem(Items.REDSTONE_BLOCK, 1).withItem(BlockRegistry.SOURCE_GEM_BLOCK, 1));
        recipes.add(get(EffectReverseDirection.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.GLASS_PANE));

        recipes.add(get(FilterBlock.INSTANCE).withIngredient(Ingredient.of(Tags.Items.COBBLESTONES)));
        recipes.add(get(FilterEntity.INSTANCE).withIngredient(Ingredient.of(Tags.Items.NUGGETS_IRON)));

        recipes.add(get(FilterItem.INSTANCE).withItem(Items.EMERALD));
        recipes.add(get(FilterLiving.INSTANCE).withItem(Items.DANDELION));
        recipes.add(get(FilterMonster.INSTANCE).withItem(Items.LILY_OF_THE_VALLEY));
        recipes.add(get(FilterAnimal.INSTANCE).withItem(Items.BEEF));
        recipes.add(get(FilterPlayer.INSTANCE).withItem(Items.POPPY));
        recipes.add(get(FilterLivingNotMonster.INSTANCE).withItem(Items.OXEYE_DAISY));
        recipes.add(get(FilterLivingNotPlayer.INSTANCE).withItem(Items.BLUE_ORCHID));

        recipes.add(get(FilterBaby.INSTANCE).withIngredient(Ingredient.of(Tags.Items.EGGS)));
        recipes.add(get(FilterMature.INSTANCE).withItem(Items.CHICKEN));

        recipes.add(get(FilterLight.LIGHT).withItem(Items.TORCH));
        recipes.add(get(FilterLight.DARK).withItem(Items.TORCH).withIngredient(Ingredient.of(Tags.Items.DYES_BLACK)));

        recipes.add(get(PropagatePlane.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(Items.DIAMOND_BLOCK).withItem(Items.FIREWORK_STAR).withItem(ItemsRegistry.WILDEN_SPIKE));
        recipes.add(get(PropagateSelf.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodSelf.INSTANCE.getGlyph()));
        recipes.add(get(PropagateProjectile.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodProjectile.INSTANCE.getGlyph()));

        recipes.add(get(PropagateUnderfoot.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodUnderfoot.INSTANCE.getGlyph()));

        recipes.add(get(MethodMissile.INSTANCE).withItem(Items.FIREWORK_ROCKET, 2).withItem(ItemsRegistry.AIR_ESSENCE).withItem(ItemsRegistry.FIRE_ESSENCE));
        recipes.add(get(PropagateMissile.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodMissile.INSTANCE.getGlyph()));

        recipes.add(get(MethodOverhead.INSTANCE).withItem(Items.IRON_HELMET).withItem(ItemsRegistry.AIR_ESSENCE));
        recipes.add(get(PropagateOverhead.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(MethodOverhead.INSTANCE.getGlyph()));

        recipes.add(get(EffectResize.INSTANCE).withItem(ItemsRegistry.MANIPULATION_ESSENCE).withItem(ItemsRegistry.ABJURATION_ESSENCE).withItem(Items.BROWN_MUSHROOM));


        Path outputBase = generator.getPackOutput().getOutputFolder();
        for (GlyphRecipe recipe : recipes)
            saveStable(pOutput, GlyphRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow(), getScribeGlyphPath(outputBase, recipe.output.getItem()));
    }

    protected static Path getScribeGlyphPath(Path pathIn, Item glyph) {
        var regName = getRegistryName(glyph);
        return pathIn.resolve("data/" + regName.getNamespace() + "/recipe/" + regName.getPath() + ".json");
    }

    public void addRecipe(AbstractSpellPart part, ItemLike... items) {
        var builder = get(part);
        for (ItemLike item : items) {
            builder.withItem(item);
        }
        recipes.add(builder);
    }
}
