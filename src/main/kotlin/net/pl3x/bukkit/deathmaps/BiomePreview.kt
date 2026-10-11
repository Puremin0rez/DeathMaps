package net.pl3x.bukkit.deathmaps

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.tags.BiomeTagKeys
import net.minecraft.world.level.saveddata.maps.MapId
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView
import org.bukkit.plugin.Plugin

// Vanilla's explorer map biome preview (MapItem.renderBiomePreviewMap) for CLOSEST scale maps.
// Biomes are sampled off the main thread and the pixels are written on it
object BiomePreview {
  private const val SIZE = 128

  // Map colors are packed as color id * 4 + shade
  private const val NONE: Byte = 0
  private const val ORANGE = 15 * 4
  private const val BROWN = 26 * 4
  private const val LOW = 0
  private const val NORMAL = 1
  private const val HIGH = 2
  private const val LOWEST = 3
  private val WAVE_SHADES = intArrayOf(LOW, NORMAL, HIGH, NORMAL, LOW)

  // Scale of vanilla's Mth.sin lookup table
  private const val SIN_SCALE = 10430.378350470453

  // The pixels fill in a few ticks after the map is created
  fun render(plugin: Plugin, map: ItemStack) {
    val mapView = checkNotNull((map.itemMeta as MapMeta).mapView) { "Item is not a filled map" }
    check(mapView.scale == MapView.Scale.CLOSEST) { "Only CLOSEST scale maps are supported" }
    val world = checkNotNull(mapView.world) { "Map ${mapView.id} has no world" }

    // Samples world noise without loading chunks
    val biomeProvider = world.vanillaBiomeProvider()
    val biomeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME)
    val wateryBiomes =
        biomeRegistry.getTag(BiomeTagKeys.WATER_ON_MAP_OUTLINES).resolve(biomeRegistry).toSet()
    val seaLevel = world.seaLevel
    // World coordinates of the map's top left pixel
    val mapMinX = mapView.centerX - SIZE / 2
    val mapMinZ = mapView.centerZ - SIZE / 2

    plugin.server.scheduler.runTaskAsynchronously(
        plugin,
        Runnable {
          val isWateryPixel =
              BooleanArray(SIZE * SIZE) { pixelIndex ->
                val blockX = mapMinX + pixelIndex % SIZE
                val blockZ = mapMinZ + pixelIndex / SIZE
                biomeProvider.getBiome(world, blockX, seaLevel, blockZ) in wateryBiomes
              }
          val pixelColors =
              ByteArray(SIZE * SIZE) { pixelIndex ->
                colorForPixel(isWateryPixel, pixelIndex % SIZE, pixelIndex / SIZE)
              }
          if (!plugin.isEnabled) return@Runnable

          plugin.server.scheduler.runTask(
              plugin,
              Runnable {
                // Saved map pixels can only be written through internals
                val mapData = (world as CraftWorld).handle.getMapData(MapId(mapView.id))
                pixelColors.forEachIndexed { pixelIndex, color ->
                  if (color != NONE) mapData?.setColor(pixelIndex % SIZE, pixelIndex / SIZE, color)
                }
              },
          )
        },
    )
  }

  // Orange along water, brown along the shore, nothing inland or on the outer edge
  private fun colorForPixel(isWateryPixel: BooleanArray, pixelX: Int, pixelY: Int): Byte {
    if (pixelX == 0 || pixelY == 0 || pixelX == SIZE - 1 || pixelY == SIZE - 1) return NONE
    val pixelIsWatery = isWateryPixel[pixelY * SIZE + pixelX]
    var wateryNeighbours = if (pixelIsWatery) -1 else 0
    for (offsetY in -1..1) {
      for (offsetX in -1..1) {
        if (isWateryPixel[(pixelY + offsetY) * SIZE + pixelX + offsetX]) wateryNeighbours++
      }
    }

    if (!pixelIsWatery) {
      if (wateryNeighbours == 0) return NONE
      return packColor(BROWN, if (wateryNeighbours > 3) NORMAL else LOWEST)
    }
    if (wateryNeighbours > 7) {
      // Open water gets wavy stripes on every other row
      if (pixelY % 2 != 0) return NONE
      val waveIndex = (pixelX + (tableSin(pixelY) * 7).toInt()) / 8 % 5
      return packColor(ORANGE, WAVE_SHADES[waveIndex])
    }
    val shade =
        when {
          wateryNeighbours > 5 -> NORMAL
          wateryNeighbours > 1 -> LOW
          else -> LOWEST
        }
    return packColor(ORANGE, shade)
  }

  private fun packColor(baseColor: Int, shade: Int): Byte = (baseColor + shade).toByte()

  // Vanilla's Mth.sin, a 65536 entry lookup table
  private fun tableSin(angle: Int): Float =
      Math.sin(((angle * SIN_SCALE).toLong() and 0xFFFF) / SIN_SCALE).toFloat()
}
