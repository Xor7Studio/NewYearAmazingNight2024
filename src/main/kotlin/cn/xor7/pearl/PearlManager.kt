package cn.xor7.pearl

import cn.xor7.map.GameMap
import cn.xor7.map.MapSection
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Location
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object PearlManager {
    private const val PEARL_CONFIG_FILE_NAME = ".nyan/pearl-conf.json"
    private lateinit var configData: PearlConfigData
    private val json = Json { prettyPrint = true }

    private val allowedSections = mutableSetOf<MapSection>()

    init {
        try {
            loadPearlConfig()
            configData.allowedSections.forEach { sectionId ->
                allowedSections.add(GameMap.getSection(sectionId)!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadPearlConfig() {
        val pearlConfigJsonFIle = File(PEARL_CONFIG_FILE_NAME)
        if (!pearlConfigJsonFIle.isFile) {
            pearlConfigJsonFIle.createNewFile()
            pearlConfigJsonFIle.writeText("{}")
        }
        try {
            configData =
                json.decodeFromString<PearlConfigData>(Files.readString(Paths.get(PEARL_CONFIG_FILE_NAME)))
            pearlConfigJsonFIle.writeText(json.encodeToString(configData))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun inAllowedLocation(loc: Location): Boolean {
        if (!configData.clearTransgressivePearls) return true
        if (allowedSections.isEmpty()) return true
        if (allowedSections.any { it.getPosition(loc) in 0.00..it.sectionLength }) return true
        return false
    }
}