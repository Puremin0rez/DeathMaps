package net.pl3x.bukkit.deathmaps.config

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import java.io.File
import java.io.IOException
import java.time.DateTimeException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.map.MapCursor
import org.bukkit.plugin.java.JavaPlugin

data class DeathMapsConfig(
    val requireDrops: Boolean,
    val disabledWorlds: Set<String>,
    val hand: EquipmentSlot,
    val markerType: MapCursor.Type,
    val biomePreview: Boolean,
    val mapName: String,
    val mapLore: List<String>,
    val dateFormat: DateTimeFormatter,
) {
  companion object {
    // Returns null when the config can't be used
    fun load(plugin: JavaPlugin): DeathMapsConfig? {
      plugin.saveDefaultConfig()
      val file = File(plugin.dataFolder, "config.yml")

      // Stops if the config can't be parsed
      try {
        YamlConfiguration().load(file)
      } catch (e: Exception) {
        plugin.logger.severe("config.yml could not be read, fix it and restart: ${e.message}")
        return null
      }
      plugin.reloadConfig()

      // Appends missing options, skipping ones present but left blank
      val defaults = plugin.config.defaults!!
      val contents = file.readText()
      val missing =
          (defaults.getKeys(false) - plugin.config.getKeys(false)).filterNot {
            Regex("^${Regex.escape(it)}:", RegexOption.MULTILINE).containsMatchIn(contents)
          }
      if (missing.isNotEmpty()) {
        plugin.logger.info("Adding missing config options: ${missing.joinToString()}")
        val additions = YamlConfiguration()
        missing.forEach { additions.set(it, defaults.get(it)) }
        val separator = if (contents.isEmpty() || contents.endsWith("\n")) "" else "\n"
        try {
          file.appendText(separator + additions.saveToString())
          plugin.reloadConfig()
        } catch (e: IOException) {
          plugin.logger.warning(
              "Couldn't add them to config.yml, using their defaults: ${e.message}"
          )
        }
      }

      return from(plugin.config) { plugin.logger.warning("config.yml: $it") }
    }

    internal fun from(config: ConfigurationSection, warn: (String) -> Unit): DeathMapsConfig {
      val defaults = config.defaultSection!!

      fun boolean(key: String): Boolean =
          when (val value = config.get(key)) {
            is Boolean -> value
            else -> {
              warn("$key must be true or false but is '$value', using the default")
              defaults.getBoolean(key)
            }
          }

      fun string(key: String): String =
          when (val value = config.get(key)) {
            is String,
            is Number,
            is Boolean -> value.toString()
            else -> {
              warn("$key must be text but is '$value', using the default")
              defaults.getString(key)!!
            }
          }

      fun stringList(key: String): List<String> {
        val entries =
            config.get(key) as? List<*>
                ?: run {
                  warn("$key must be a list, using the default")
                  return defaults.getStringList(key)
                }
        return entries.mapIndexedNotNull { i, entry ->
          when (entry) {
            is String,
            is Number,
            is Boolean -> entry.toString()
            else -> {
              warn("$key entry ${i + 1} skipped: must be text but is '$entry'")
              null
            }
          }
        }
      }

      // Formats once to validate the pattern
      val dateFormat =
          string("dateFormat").let { pattern ->
            try {
              DateTimeFormatter.ofPattern(pattern).also { it.format(ZonedDateTime.now()) }
            } catch (e: DateTimeException) {
              warn("dateFormat '$pattern' is invalid (${e.message}), using the default")
              DateTimeFormatter.ofPattern(defaults.getString("dateFormat")!!)
            } catch (e: IllegalArgumentException) {
              warn("dateFormat '$pattern' is invalid (${e.message}), using the default")
              DateTimeFormatter.ofPattern(defaults.getString("dateFormat")!!)
            }
          }

      fun handOf(name: String): EquipmentSlot? =
          when (name.lowercase()) {
            "off_hand" -> EquipmentSlot.OFF_HAND
            "main_hand" -> EquipmentSlot.HAND
            else -> null
          }

      val handName = string("hand")
      val hand =
          handOf(handName)
              ?: run {
                warn("hand must be off_hand or main_hand but is '$handName', using the default")
                handOf(defaults.getString("hand")!!)!!
              }

      val markerTypes = RegistryAccess.registryAccess().getRegistry(RegistryKey.MAP_DECORATION_TYPE)
      val markerType =
          string("markerType").let { name ->
            NamespacedKey.fromString(name.lowercase())?.let { markerTypes.get(it) }
                ?: run {
                  val valid = markerTypes.joinToString { it.key.key }
                  warn(
                      "markerType '$name' isn't a map icon, using the default. Valid icons: $valid"
                  )
                  markerTypes.getOrThrow(
                      NamespacedKey.fromString(defaults.getString("markerType")!!)!!
                  )
                }
          }

      return DeathMapsConfig(
          requireDrops = boolean("requireDrops"),
          disabledWorlds = stringList("disabledWorlds").toSet(),
          hand = hand,
          markerType = markerType,
          biomePreview = boolean("biomePreview"),
          mapName = string("mapName"),
          mapLore = stringList("mapLore"),
          dateFormat = dateFormat,
      )
    }
  }
}
