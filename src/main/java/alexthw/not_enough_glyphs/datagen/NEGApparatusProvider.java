package alexthw.not_enough_glyphs.datagen;

import alexthw.ars_elemental.ArsElemental;
import alexthw.ars_elemental.registry.ModItems;
import alexthw.not_enough_glyphs.common.spell.*;
import alexthw.not_enough_glyphs.init.Registry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ApparatusRecipeBuilder;
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
import java.util.ArrayList;
import java.util.List;

import static alexthw.not_enough_glyphs.init.NotEnoughGlyphs.prefix;

public class NEGApparatusProvider extends ApparatusRecipeProvider {
    public NEGApparatusProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    static TagKey<Item> SPELLBOOKS = TagKey.create(Registries.ITEM, ArsNouveau.prefix("spellbook"));

    @Override
    public void collectJsons(CachedOutput pOutput) {
        List<ApparatusRecipeBuilder.RecipeWrapper<? extends EnchantingApparatusRecipe>> elementalList = new ArrayList<>();

        addRecipe(builder().withReagent(Ingredient.of(SPELLBOOKS)).withPedestalItem(8, Items.LEATHER).withResult(Registry.SPELL_BINDER.get()).withId(prefix("spell_binder")).build());
        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(ItemsRegistry.SHAPERS_FOCUS).withPedestalItem(2, ItemsRegistry.MANIPULATION_ESSENCE).withResult(getPerkItem(FocusPerk.MANIPULATION.getRegistryName())).build());
        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(ItemsRegistry.SUMMONING_FOCUS).withPedestalItem(2, ItemsRegistry.CONJURATION_ESSENCE).withResult(getPerkItem(FocusPerk.SUMMONING.getRegistryName())).build());
        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(Items.ENDER_PEARL).withPedestalItem(Items.RABBIT_FOOT).withPedestalItem(Items.BONE).withResult(getPerkItem(RandomPerk.INSTANCE.getRegistryName())).build());
        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(Items.GHAST_TEAR).withPedestalItem(Items.FEATHER).withPedestalItem(Items.EMERALD).withResult(getPerkItem(PacificThread.INSTANCE.getRegistryName())).build());
        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(Items.IRON_BLOCK).withPedestalItem(Items.BLAZE_POWDER).withPedestalItem(Items.NETHERITE_SCRAP).withResult(getPerkItem(BulldozeThread.INSTANCE.getRegistryName())).build());

        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(Items.DIAMOND_SWORD).withPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE).withPedestalItem(Items.FLINT).withResult(getPerkItem(SharpThread.INSTANCE.getRegistryName())).build());
        addRecipe(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(Items.PISTON).withPedestalItem(ItemsRegistry.AIR_ESSENCE).withPedestalItem(Items.IRON_BLOCK).withResult(getPerkItem(PounchThread.INSTANCE.getRegistryName())).build());

        elementalList.add(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(ModItems.LESSER_FIRE_FOCUS.get()).withPedestalItem(2, ItemsRegistry.FIRE_ESSENCE).withResult(getPerkItem(FocusPerk.ELEMENTAL_FIRE.getRegistryName())).build());
        elementalList.add(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(ModItems.LESSER_WATER_FOCUS.get()).withPedestalItem(2, ItemsRegistry.WATER_ESSENCE).withResult(getPerkItem(FocusPerk.ELEMENTAL_WATER.getRegistryName())).build());
        elementalList.add(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(ModItems.LESSER_EARTH_FOCUS.get()).withPedestalItem(2, ItemsRegistry.EARTH_ESSENCE).withResult(getPerkItem(FocusPerk.ELEMENTAL_EARTH.getRegistryName())).build());
        elementalList.add(builder().withReagent(ItemsRegistry.BLANK_THREAD).withPedestalItem(ModItems.LESSER_AIR_FOCUS.get()).withPedestalItem(2, ItemsRegistry.AIR_ESSENCE).withResult(getPerkItem(FocusPerk.ELEMENTAL_AIR.getRegistryName())).build());


        for (var recipe : recipes) {
            Path path = getRecipePath(this.output, recipe.id());
            this.saveStable(pOutput, recipe.serialize(), path);
        }

        for (var recipe : elementalList) {
            Path path = getRecipePath(this.output, recipe.id().getPath());
            var json = addModLoadedCondition(recipe.serialize(), ArsElemental.MODID);
            // wrap json with conditional block that checks if the mod is loaded

            this.saveStable(pOutput, json, path);
        }

    }

    protected static Path getRecipePath(Path pathIn, ResourceLocation str) {
        return pathIn.resolve("data/" + str.getNamespace() + "/recipe/" + str.getPath() + ".json");
    }

    public static JsonObject addModLoadedCondition(JsonElement recipeElement, String modId) {
        JsonObject recipe = recipeElement.getAsJsonObject();

        JsonObject condition = new JsonObject();
        condition.addProperty("type", "neoforge:mod_loaded");
        condition.addProperty("modid", modId);

        JsonArray conditions = new JsonArray();
        conditions.add(condition);
        recipe.add("neoforge:conditions", conditions);
        return recipe;
    }
}
