package net.pl3x.bukkit.deathmaps

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore
import io.papermc.paper.datacomponent.item.MapDecorations
import io.papermc.paper.datacomponent.item.MapId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.map.MapCursor
import org.bukkit.map.MapView

object DeathMapFactory {
  // CLOSEST scale maps cover 128x128 blocks
  private val MAP_SCALE = MapView.Scale.CLOSEST
  private const val MAP_SIZE = 128
  private const val MARKER_KEY = "Death"
  private val dateFormat = DateTimeFormatter.ofPattern("M/d/yy h:mm a")

  // Explorer style map marking where the player died
  fun create(player: Player, location: Location): ItemStack {
    val map = ItemStack.of(Material.FILLED_MAP)
    map.setData(DataComponentTypes.MAP_ID, MapId.mapId(createMapView(location).id))
    map.setData(DataComponentTypes.MAP_DECORATIONS, deathMarker(location))
    map.setData(DataComponentTypes.CUSTOM_NAME, name(player))
    map.setData(DataComponentTypes.LORE, lore(location))
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
            MapCursor.Type.RED_X,
            location.blockX.toDouble(),
            location.blockZ.toDouble(),
            180.0f,
        )
    return MapDecorations.mapDecorations(mapOf(MARKER_KEY to marker))
  }

  private fun name(player: Player): Component {
    val suffix = text("'s Death Map", NamedTextColor.RED).nonItalic()
    return player.displayName().append(suffix)
  }

  private fun lore(location: Location): ItemLore {
    val world =
        text("World: ", NamedTextColor.YELLOW)
            .append(text(location.world.name, NamedTextColor.WHITE, TextDecoration.UNDERLINED))
    val coordinates =
        text("X: ", NamedTextColor.DARK_AQUA)
            .append(text(location.blockX, NamedTextColor.WHITE))
            .append(text(" Y: ", NamedTextColor.DARK_AQUA))
            .append(text(location.blockY, NamedTextColor.WHITE))
            .append(text(" Z: ", NamedTextColor.DARK_AQUA))
            .append(text(location.blockZ, NamedTextColor.WHITE))
    val time = text(dateFormat.format(LocalDateTime.now()), NamedTextColor.GOLD)
    return ItemLore.lore(listOf(world, coordinates, time).map { it.nonItalic() })
  }

  private fun Component.nonItalic(): Component = decoration(TextDecoration.ITALIC, false)
}
