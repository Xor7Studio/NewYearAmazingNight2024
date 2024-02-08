package cn.xor7.tgttos

import cn.xor7.readConfig

object TgttosManager {
    private const val TGTTOS_CONFIG_FILE_NAME = ".nyan/tgttos-conf.json"
    private lateinit var configData: TgttosConfigData

    private val allowedSections = mutableSetOf<Int>()

    init {
        try {
            configData = readConfig(TGTTOS_CONFIG_FILE_NAME)
            allowedSections.addAll(configData.allowedSections)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun allowedSectionsContains(section: Int) = allowedSections.contains(section)
}