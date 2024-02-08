package cn.xor7.spectator

import cn.xor7.readConfig
import org.bukkit.entity.Player

object SpectatorManager {
    private const val SPECTATOR_CONFIG_FILE_NAME = ".nyan/spectator-conf.json"
    private lateinit var configData: SpectatorConfigData

    private val spectators = mutableSetOf<String>()

    init {
        try {
            configData = readConfig(SPECTATOR_CONFIG_FILE_NAME)
            spectators.addAll(configData.spectators)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val Player.isSpectator: Boolean
        get() = spectators.contains(name)
}