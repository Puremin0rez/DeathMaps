package net.pl3x.bukkit.deathmaps.listeners

import net.pl3x.bukkit.deathmaps.BiomePreview
import net.pl3x.bukkit.deathmaps.DeathMapFactory
import net.pl3x.bukkit.deathmaps.config.DeathMapsConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class DeathListener(
    private val plugin: Plugin,
    private val config: DeathMapsConfig,
    private val deathMapFactory: DeathMapFactory,
) : Listener {
  // Deaths that dropped items, recorded before other plugins can clear the drops
  private val deathsWithDrops = mutableSetOf<PlayerDeathEvent>()

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  fun recordDrops(event: PlayerDeathEvent) {
    if (event.drops.isNotEmpty()) deathsWithDrops.add(event)
  }

  @EventHandler(priority = EventPriority.MONITOR)
  fun onDeath(event: PlayerDeathEvent) {
    val droppedItems =
        deathsWithDrops.remove(event) && !(event.keepInventory && event.drops.isEmpty())
    if (event.isCancelled || (config.requireDrops && !droppedItems)) return
    val player = event.entity
    if (!player.hasPermission(RECEIVE_PERMISSION) || player.world.name in config.disabledWorlds)
        return
    val map = deathMapFactory.create(player, player.location)
    if (config.biomePreview) BiomePreview.render(plugin, map)

    plugin.server.scheduler.runTaskLater(plugin, Runnable { giveMap(player, map) }, 1L)
  }

  private fun giveMap(player: Player, map: ItemStack) {
    val inventory = player.inventory
    val otherHand =
        if (config.hand == EquipmentSlot.HAND) EquipmentSlot.OFF_HAND else EquipmentSlot.HAND
    val emptyHand = listOf(config.hand, otherHand).firstOrNull { inventory.getItem(it).isEmpty }
    if (emptyHand != null) {
      inventory.setItem(emptyHand, map)
    } else {
      inventory.addItem(map)
    }
  }

  private companion object {
    const val RECEIVE_PERMISSION = "deathmaps.receive"
  }
}
