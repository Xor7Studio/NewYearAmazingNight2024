package cn.xor7

import cn.xor7.map.GameMap
import cn.xor7.map.MapListener
import cn.xor7.map.PlayerTrackerData
import cn.xor7.pearl.PearlListener
import cn.xor7.scoreboard.ScoreboardManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

val instance by lazy { JavaPlugin.getPlugin(NewYearAmazingNight::class.java) }

@Suppress("unused")
class NewYearAmazingNight : JavaPlugin() {
    private val apiServer = embeddedServer(Netty, 8000) {
        setupApplication()
    }.start(wait = false)

    private fun Application.setupApplication() {
        install(CORS) {
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowHeader(HttpHeaders.ContentType)
            allowNonSimpleContentTypes = true
            allowCredentials = true
            allowSameOrigin = true
            anyHost()
        }

        routing {
            get("/data") {
                call.respondText(
                    text = Json.encodeToString(mutableListOf<PlayerTrackerData>().apply {
                        GameMap.trackers.forEach { (_, tracker) ->
                            this += tracker.getData()
                        }
                    }),
                    contentType = ContentType.Application.Json
                )
            }
        }
    }

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

    override fun onDisable() {
        apiServer.stop()
    }
}

fun Player.sendToSpawnPoint() {
    getTracker()!!.apply {
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

fun Player.getTracker() = GameMap.trackers[name]

fun JavaPlugin.runLater(delay: Long, task: BukkitRunnable.() -> Unit) {
    object : BukkitRunnable() {
        override fun run() {
            this.task()
        }
    }.runTaskLater(this, delay)
}

fun Component.legacyText() = LegacyComponentSerializer.legacyAmpersand().serialize(this)