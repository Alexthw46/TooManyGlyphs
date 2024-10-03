# Not Enough Glyphs!

An extension for [Ars Nouveau](https://www.curseforge.com/minecraft/mc-mods/ars-nouveau) with diverse sources of inspiration.
It features a great many glyphs, some new and some repacked from older Addons that slow to update (but will work with
NEG and their versions will have priority, it's not a full replacement). Quite possibly too many of them, but still not
enough of them.

Every push to this repository is built and published to the [BlameJared](https://maven.blamejared.com) maven, to use
these builds in your project, simply add the following code in your build.gradle

```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    implementation fg.deobf("com.alexthw.not_enough_glyphs:not_enough_glyphs-[MC_VERSION]:[VERSION]")
}
```

Current version (1.21.0 for file name, actually 1.21.1):
[![Maven](https://img.shields.io/maven-metadata/v?label=&color=C71A36&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Falexthw%2Fnot_enough_glyphs%2Fnot_enough_glyphs-1.21.0%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/alexthw/not_enough_glyphs/not_enough_glyphs-1.21.0/)

(remove the v)
