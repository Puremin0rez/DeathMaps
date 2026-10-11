package net.pl3x.bukkit.deathmaps.listeners

import net.pl3x.bukkit.deathmaps.BiomePreview
import net.pl3x.bukkit.deathmaps.DeathMapFactory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class DeathListener(private val plugin: Plugin) : Listener {
  // Deaths that dropped items, recorded before other plugins can clear the drops
  private val deathsWithDrops = mutableSetOf<PlayerDeathEvent>()

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  fun recordDrops(event: PlayerDeathEvent) {
    if (event.drops.isNotEmpty()) deathsWithDrops.add(event)
  }

  @EventHandler(priority = EventPriority.MONITOR)
  fun onDeath(event: PlayerDeathEvent) {
    if (!deathsWithDrops.remove(event) || event.isCancelled) return
    if (event.keepInventory && event.drops.isEmpty()) return
    val player = event.entity
    val map = DeathMapFactory.create(player, player.location)
    BiomePreview.render(plugin, map)

    plugin.server.scheduler.runTaskLater(plugin, Runnable { giveMap(player, map) }, 1L)
  }

  private fun giveMap(player: Player, map: ItemStack) {
    val inventory = player.inventory
    if (inventory.itemInOffHand.isEmpty) {
      inventory.setItemInOffHand(map)
    } else {
      inventory.addItem(map)
    }
  }
}
