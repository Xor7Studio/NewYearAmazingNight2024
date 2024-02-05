package cn.xor7

import cn.xor7.map.GameMap
import cn.xor7.map.MapListener
import cn.xor7.map.PlayerTracker
import cn.xor7.pearl.PearlListener
import cn.xor7.scoreboard.ScoreboardManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

val instance by lazy { JavaPlugin.getPlugin(NewYearAmazingNight::class.java) }

@Suppress("unused")
class NewYearAmazingNight : JavaPlugin() {
    // private val apiServer = embeddedServer(Netty, 8000) {
    //     setupApplication()
    // }.start(wait = false)
    //
    // private fun Application.setupApplication() {
    //     install(CORS) {
    //         allowHeader(HttpHeaders.AccessControlAllowOrigin)
    //         allowHeader(HttpHeaders.ContentType)
    //         allowNonSimpleContentTypes = true
    //         allowCredentials = true
    //         allowSameOrigin = true
    //         anyHost()
    //     }
    //
    //     routing {
    //         get("/data") {
    //             call.respondText(
    //                 text = Json.encodeToString(mutableListOf<PlayerTrackerData>().apply {
    //                     GameMap.trackers.forEach { (_, tracker) ->
    //                         this += tracker.getData()
    //                     }
    //                 }),
    //                 contentType = ContentType.Application.Json
    //             )
    //         }
    //     }
    // }

    override fun onEnable() {
        server.pluginManager.registerEvents(MapListener, this)
        server.pluginManager.registerEvents(PearlListener, this)
        Command.register()
        object : BukkitRunnable() {
            override fun run() {
                GameMap.tick()
                ScoreboardManager.updateScoreboard()
            }
        }.runTaskTimer(this, 0L, 20L)
    }

    // override fun onDisable() = apiServer.stop()
}

fun Player.sendToSpawnPoint() {
    tracker!!.apply {
        nowSectionId = GameMap.playerSpawnInfo[name]?.second ?: 0
        invincible = true
        instance.runLater(20L) {
            invincible = false
        }
    }
    teleport(
        GameMap.playerSpawnInfo[name]?.first
            ?: bedSpawnLocation
            ?: world.spawnLocation
    )
}

val Player.tracker: PlayerTracker?
    get() = GameMap.trackers[name]

fun Player.removeBambooRaft() {
    inventory.forEach {
        if (it?.type == Material.BAMBOO_RAFT)
            inventory.remove(it)
    }
}

fun Player.removeEnderPearl() {
    inventory.forEach {
        if (it?.type == Material.ENDER_PEARL)
            inventory.remove(it)
    }
}

fun JavaPlugin.runLater(delay: Long, task: BukkitRunnable.() -> Unit) {
    object : BukkitRunnable() {
        override fun run() {
            this.task()
        }
    }.runTaskLater(this, delay)
}

fun Cancellable.cancelInNonDevMode(player: Player) {
    val tracker = player.tracker ?: run {
        isCancelled = true
        return
    }
    if (!tracker.developmentMode) isCancelled = true
}
