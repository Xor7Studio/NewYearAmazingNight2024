package cn.xor7

import cn.xor7.command.DevelopmentModeCommand
import cn.xor7.map.GameMap
import co.aikar.commands.PaperCommandManager
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class NewYearAmazingNight : JavaPlugin() {
    private lateinit var commandManager: PaperCommandManager
    override fun onEnable() {
        server.pluginManager.registerEvents(Listener(), this)
        commandManager = PaperCommandManager(this)
        commandManager.registerCommand(DevelopmentModeCommand())
        object : BukkitRunnable() {
            override fun run() = GameMap.tick()
        }.runTaskTimer(this, 0L, 20L)
    }
}

fun Player.sendToSpawnPoint() {
    this.teleport(this.bedSpawnLocation ?: this.world.spawnLocation)
}