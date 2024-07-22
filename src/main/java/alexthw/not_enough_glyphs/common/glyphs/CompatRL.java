package alexthw.not_enough_glyphs.common.glyphs;

import net.minecraft.resources.ResourceLocation;

public class CompatRL {
    public static ResourceLocation tmg(String path) {
        return ResourceLocation.fromNamespaceAndPath("toomanyglyphs", "glyph_" + path);
    }

    public static ResourceLocation omega(String path) {
        return ResourceLocation.fromNamespaceAndPath("arsomega", "glyph_" + path);
    }
    public static ResourceLocation elemental(String path) {
        return ResourceLocation.fromNamespaceAndPath("ars_elemental", "glyph_" + path);
    }

    public static ResourceLocation neg(String path) {
        return ResourceLocation.fromNamespaceAndPath("not_enough_glyphs", "glyph_" + path);
    }

}
