package cn.xor7

import cn.xor7.map.GameMap
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class NewYearAmazingNight : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(Listener(), this)
        object : BukkitRunnable() {
            override fun run() = GameMap.tick()
        }.runTaskTimer(this, 0L, 20L)
    }
}