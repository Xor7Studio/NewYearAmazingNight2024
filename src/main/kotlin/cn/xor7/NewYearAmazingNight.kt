package cn.xor7

import cn.xor7.command.DevelopmentModeCommand
import cn.xor7.command.ParticleCommand
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
        commandManager.registerCommand(ParticleCommand())
        object : BukkitRunnable() {
            override fun run() = GameMap.tick()
        }.runTaskTimer(this, 0L, 20L)
    }
}

fun Player.sendToSpawnPoint() {
    this.teleport(this.bedSpawnLocation ?: this.world.spawnLocation)
}

fun Player.toggleDevelopmentMode(): Boolean {
    return if (GameMap.trackers[this.name]!!.developmentMode) {
        GameMap.trackers[this.name]!!.developmentMode = false
        this.sendMessage("§a已关闭开发者模式")
        false
    } else {
        GameMap.trackers[this.name]!!.developmentMode = true
        this.sendMessage("§a已开启开发者模式")
        true
    }
}