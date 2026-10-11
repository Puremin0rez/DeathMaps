package net.pl3x.bukkit.deathmaps

import net.pl3x.bukkit.deathmaps.config.DeathMapsConfig
import net.pl3x.bukkit.deathmaps.listeners.DeathListener
import org.bukkit.plugin.java.JavaPlugin

class DeathMaps : JavaPlugin() {
  override fun onEnable() {
    val config = DeathMapsConfig.load(this)
    if (config == null) {
      server.pluginManager.disablePlugin(this)
      return
    }
    server.pluginManager.registerEvents(DeathListener(this, config, DeathMapFactory(config)), this)
  }
}
