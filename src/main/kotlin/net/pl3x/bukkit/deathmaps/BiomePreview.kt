package net.pl3x.bukkit.deathmaps

import net.minecraft.world.item.MapItem
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta

// Vanilla's explorer map biome preview (MapItem.renderBiomePreviewMap)
object BiomePreview {
  fun render(map: ItemStack) {
    val mapView = checkNotNull((map.itemMeta as MapMeta).mapView) { "Item is not a filled map" }
    val world = checkNotNull(mapView.world) { "Map ${mapView.id} has no world" }
    MapItem.renderBiomePreviewMap((world as CraftWorld).handle, CraftItemStack.asNMSCopy(map))
  }
}
