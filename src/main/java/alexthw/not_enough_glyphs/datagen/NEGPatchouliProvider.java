package alexthw.not_enough_glyphs.datagen;

import alexthw.not_enough_glyphs.common.spell.*;
import alexthw.not_enough_glyphs.init.ArsNouveauRegistry;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.ApparatusPage;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.GlyphScribePage;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.PatchouliBuilder;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.TextPage;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

public class NEGPatchouliProvider extends PatchouliProvider {
    public NEGPatchouliProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput cache) {
        for (AbstractSpellPart part : ArsNouveauRegistry.registeredSpells) this.addGlyphPage(part);
        addBasicItem(Registry.SPELL_BINDER.get(), EQUIPMENT, new ApparatusPage(Registry.SPELL_BINDER.get()));
        addPerkPage(BulldozeThread.INSTANCE);
        addPerkPage(PacificThread.INSTANCE);
        addPerkPage(SharpThread.INSTANCE);
        addPerkPage(PounchThread.INSTANCE);
        addPerkPage(RandomPerk.INSTANCE);
        PatchouliBuilder builder = new PatchouliBuilder(EQUIPMENT, "focus_threads")
                .withIcon(PerkRegistry.getPerkItemMap().get(FocusPerk.MANIPULATION.getRegistryName()))
                .withTextPage("ars_nouveau.page.focus_threads.desc")
                .withPage(new ApparatusPage(PerkRegistry.getPerkItemMap().get(FocusPerk.SUMMONING.getRegistryName())))
                .withPage(new ApparatusPage(PerkRegistry.getPerkItemMap().get(FocusPerk.MANIPULATION.getRegistryName())))
                .withPage(new ApparatusPage(PerkRegistry.getPerkItemMap().get(FocusPerk.ELEMENTAL_FIRE.getRegistryName())))
                .withPage(new ApparatusPage(PerkRegistry.getPerkItemMap().get(FocusPerk.ELEMENTAL_AIR.getRegistryName())))
                .withPage(new ApparatusPage(PerkRegistry.getPerkItemMap().get(FocusPerk.ELEMENTAL_EARTH.getRegistryName())))
                .withPage(new ApparatusPage(PerkRegistry.getPerkItemMap().get(FocusPerk.ELEMENTAL_WATER.getRegistryName())))
                .withSortNum(99);
        this.pages.add(new PatchouliPage(builder, getPath(EQUIPMENT, "focus_threads.json")));


        for (PatchouliPage page : pages) saveStable(cache, page.build(), page.path());
    }

    @Override
    public void addGlyphPage(AbstractSpellPart spellPart) {
        ResourceLocation category = switch (spellPart.defaultTier().value) {
            case 1 -> GLYPHS_1;
            case 2 -> GLYPHS_2;
            default -> GLYPHS_3;
        };
        PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                .withName(spellPart.getRegistryName().getNamespace() + ".glyph_name." + spellPart.getRegistryName().getPath())
                .withIcon(spellPart.getRegistryName().toString())
                .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                .withPage(new TextPage(spellPart.getRegistryName().getNamespace() + ".glyph_desc." + spellPart.getRegistryName().getPath()))
                .withPage(new GlyphScribePage(spellPart));
        if (!spellPart.getRegistryName().getNamespace().equals(NotEnoughGlyphs.MODID))
            builder.withProperty("!flag", "mod:" + spellPart.getRegistryName().getNamespace());
        this.pages.add(new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath())));
    }

    @Override
    public void addPerkPage(IPerk perk) {
        PerkItem perkItem = PerkRegistry.getPerkItemMap().get(perk.getRegistryName());
        PatchouliBuilder builder = new PatchouliBuilder(EQUIPMENT, perkItem)
                .withIcon(perkItem)
                .withTextPage(perk.getDescriptionKey())
                .withPage(new ApparatusPage(perkItem)).withSortNum(99);
        this.pages.add(new PatchouliPage(builder, getPath(EQUIPMENT, perk.getRegistryName().getPath() + ".json")));
    }

}
