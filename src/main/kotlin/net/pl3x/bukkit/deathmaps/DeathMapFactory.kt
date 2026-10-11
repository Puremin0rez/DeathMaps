package net.pl3x.bukkit.deathmaps

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore
import io.papermc.paper.datacomponent.item.MapDecorations
import io.papermc.paper.datacomponent.item.MapId
import java.time.ZonedDateTime
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.pl3x.bukkit.deathmaps.config.DeathMapsConfig
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.map.MapView

class DeathMapFactory(private val config: DeathMapsConfig) {
  // Explorer style map marking where the player died
  fun create(player: Player, location: Location): ItemStack {
    val map = ItemStack.of(Material.FILLED_MAP)
    map.setData(DataComponentTypes.MAP_ID, MapId.mapId(createMapView(location).id))
    map.setData(DataComponentTypes.MAP_DECORATIONS, deathMarker(location))

    val placeholders = placeholders(player, location)
    if (config.mapName.isNotBlank()) {
      map.setData(DataComponentTypes.CUSTOM_NAME, render(config.mapName, placeholders))
    }
    if (config.mapLore.isNotEmpty()) {
      map.setData(
          DataComponentTypes.LORE,
          ItemLore.lore(config.mapLore.map { render(it, placeholders) }),
      )
    }
    return map
  }

  private fun createMapView(location: Location): MapView {
    val mapView = Bukkit.createMap(location.world)
    mapView.scale = MAP_SCALE
    mapView.centerX = snapToMapGrid(location.blockX)
    mapView.centerZ = snapToMapGrid(location.blockZ)
    mapView.isTrackingPosition = true
    mapView.isUnlimitedTracking = true
    return mapView
  }

  private fun snapToMapGrid(coordinate: Int): Int {
    val gridCell = Math.floorDiv(coordinate + 64, MAP_SIZE)
    return gridCell * MAP_SIZE + MAP_SIZE / 2 - 64
  }

  private fun deathMarker(location: Location): MapDecorations {
    val marker =
        MapDecorations.decorationEntry(
            config.markerType,
            location.blockX.toDouble(),
            location.blockZ.toDouble(),
            180.0f,
        )
    return MapDecorations.mapDecorations(mapOf(MARKER_KEY to marker))
  }

  private fun placeholders(player: Player, location: Location): TagResolver =
      TagResolver.resolver(
          Placeholder.component("player", player.displayName()),
          Placeholder.unparsed("name", player.name),
          Placeholder.unparsed("world", location.world.name),
          Placeholder.unparsed("x", location.blockX.toString()),
          Placeholder.unparsed("y", location.blockY.toString()),
          Placeholder.unparsed("z", location.blockZ.toString()),
          Placeholder.unparsed("date", config.dateFormat.format(ZonedDateTime.now())),
      )

  // Not italic unless the text uses <italic>
  private fun render(text: String, placeholders: TagResolver): Component =
      MiniMessage.miniMessage()
          .deserialize(text, placeholders)
          .decorationIfAbsent(
              TextDecoration.ITALIC,
              TextDecoration.State.FALSE,
          )

  private companion object {
    // CLOSEST scale maps cover 128x128 blocks
    val MAP_SCALE = MapView.Scale.CLOSEST
    const val MAP_SIZE = 128
    const val MARKER_KEY = "Death"
  }
}
