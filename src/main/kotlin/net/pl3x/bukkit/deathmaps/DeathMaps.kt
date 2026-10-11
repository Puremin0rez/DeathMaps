package net.pl3x.bukkit.deathmaps

import net.pl3x.bukkit.deathmaps.listeners.DeathListener
import org.bukkit.plugin.java.JavaPlugin

class DeathMaps : JavaPlugin() {
  override fun onEnable() {
    server.pluginManager.registerEvents(DeathListener(this), this)
  }
}
