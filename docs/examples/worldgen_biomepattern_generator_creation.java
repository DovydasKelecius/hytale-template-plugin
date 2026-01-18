// // Define point generator for biome placement
// IPointGenerator pointGenerator = /* ... */;
// // Create weighted map of tile biomes
// IWeightedMap<TileBiome> tileBiomes = /* ... */;
// // Add biomes with weights:
// // - Higher weight = more common
// // - Weights are relative to each other
// // Create array of custom biomes
// CustomBiome[] customBiomes = new CustomBiome[] {
//     // Custom biomes that can override tile biomes
// };
// BiomePatternGenerator generator = new BiomePatternGenerator(
//     pointGenerator,
//     tileBiomes,
//     customBiomes
// );
// // Use in world generation
// Biome biome = generator.generateBiomeAt(zoneResult, seed, x, z);