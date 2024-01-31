package cn.xor7.pearl

import cn.xor7.map.GameMap
import cn.xor7.map.MapSection
import cn.xor7.readConfig
import org.bukkit.Location

object PearlManager {
    private const val PEARL_CONFIG_FILE_NAME = ".nyan/pearl-conf.json"
    private lateinit var configData: PearlConfigData

    private val allowedSections = mutableSetOf<MapSection>()

    init {
        try {
            configData = readConfig(PEARL_CONFIG_FILE_NAME)
            configData.allowedSections.forEach { sectionId ->
                allowedSections.add(GameMap.getSection(sectionId)!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun allowedSectionsContains(section: MapSection) = allowedSections.contains(section)

    fun inAllowedLocation(loc: Location): Boolean {
        if (!configData.clearTransgressivePearls) return true
        if (allowedSections.isEmpty()) return true
        if (allowedSections.any { it.getPosition(loc) in 0.00..it.sectionLength }) return true
        return false
    }
}