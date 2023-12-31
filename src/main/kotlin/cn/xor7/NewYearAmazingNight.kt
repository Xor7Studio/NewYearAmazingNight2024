package cn.xor7

import cn.xor7.map.GameMap
import cn.xor7.map.PlayerTrackerData
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
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

@Suppress("unused")
class NewYearAmazingNight : JavaPlugin() {
    private val apiServer = embeddedServer(Netty, 8080) {
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
        server.pluginManager.registerEvents(Listener, this)
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