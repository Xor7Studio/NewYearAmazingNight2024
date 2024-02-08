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
        bukkitRunnable {
            GameMap.tick()
            ScoreboardManager.updateScoreboard()
        }.runTaskTimer(this, 0L, 20L)
    }

    // override fun onDisable() = apiServer.stop()
}

fun Player.sendToSpawnPoint() {
    tracker!!.apply {
        nowSectionId = GameMap.playerSpawnInfo[name]?.second ?: 0
        invincible = true
        bukkitRunnable {
            invincible = false
        }.runTaskLater(instance, 20L)
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

fun Cancellable.cancelWhenPlaying(player: Player) {
    val tracker = player.tracker ?: run {
        isCancelled = true
        return
    }
    if (tracker.over) {
        isCancelled = false
        return
    }
    if (!tracker.developmentMode) isCancelled = true
}

fun Int.toOrdinal(): String {
    val suffixes = mapOf(1 to "st", 2 to "nd", 3 to "rd")

    return when {
        this % 100 in 11..13 -> "${this}th"
        this % 10 in suffixes.keys -> "${this}${suffixes[this % 10]}"
        else -> "${this}th"
    }
}

fun bukkitRunnable(block: () -> Unit): BukkitRunnable {
    return object : BukkitRunnable() {
        override fun run() {
            block()
        }
    }
}