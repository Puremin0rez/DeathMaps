package net.pl3x.bukkit.deathmaps;

import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.ItemWorldMap;
import net.minecraft.server.v1_15_R1.MapIcon;
import net.minecraft.server.v1_15_R1.WorldMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class DeathMaps extends JavaPlugin {
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onDeath(PlayerDeathEvent event) {
                Player player = event.getEntity();
                Location loc = player.getLocation();
                BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                ItemStack nmsMap = ItemWorldMap.createFilledMapView(((CraftWorld) loc.getWorld()).getHandle(), pos.getX(), pos.getZ(), MapView.Scale.CLOSEST.getValue(), true, true);
                ItemWorldMap.applySepiaFilter(((CraftWorld) loc.getWorld()).getHandle(), nmsMap);
                WorldMap.decorateMap(nmsMap, pos, "Death", MapIcon.Type.TARGET_X);
                org.bukkit.inventory.ItemStack bukkitMap = CraftItemStack.asCraftMirror(nmsMap);
                ItemMeta mapMeta = bukkitMap.getItemMeta();
                mapMeta.setDisplayName(player.getDisplayName() + ChatColor.RED + "'s Death Map");
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "World: " + player.getWorld().getName());
                mapMeta.setLore(lore);
                bukkitMap.setItemMeta(mapMeta);
                new BukkitRunnable() {
                    public void run() {
                        player.getInventory().addItem(bukkitMap);
                    }
                }.runTaskLater(DeathMaps.this, 1L);
            }
        }, this);
    }
}
