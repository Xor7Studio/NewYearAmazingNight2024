package cn.xor7.gravel

import cn.xor7.readConfig
import org.bukkit.entity.Player

object GravelManager {
    private lateinit var configData: GravelConfigData
    private const val GRAVEL_CONFIG_FILE_NAME = ".nyan/gravel-conf.json"
    var pass = false

    init {
        try {
            configData = readConfig(GRAVEL_CONFIG_FILE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTargetPlayerName() = configData.targetPlayer

    fun getTargetSectionId() = configData.targetSection

    fun Player.canPassGravelSection(): Boolean {
        if (pass) return true
        if (name == configData.targetPlayer) return true
        return false
    }
}