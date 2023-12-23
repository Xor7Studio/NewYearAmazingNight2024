package cn.xor7.map

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("MemberVisibilityCanBePrivate", "unused")
object GameMap {
    internal val trackers = mutableMapOf<String, PlayerTracker>()
    internal val ranking = mutableListOf<PlayerTracker>()
    private val sections = mutableMapOf<Int, MapSection>()
    private val lengthPrefixSum = mutableMapOf<Int, Double>()
    private val json = Json { prettyPrint = true }

    init {
        try {
            loadMap()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun tick() {
        trackers.forEach { (_, tracker) ->
            tracker.trackNowSection()
        }
        ranking.sortBy { tracker -> tracker.nowPosition }
    }

    fun getRanking() = ranking.toList()

    fun getSection(sectionId: Int) = sections[sectionId]

    fun getLengthPrefixSum(sectionId: Int) = lengthPrefixSum[sectionId] ?: 0.0

    fun loadMap() {
        sections.clear()
        lengthPrefixSum.clear()
        val mapJsonFile = File("map.json")
        if (!mapJsonFile.isFile) mapJsonFile.createNewFile()
        try {
            val gameMapData = json.decodeFromString<GameMapData>(Files.readString(Paths.get("map.json")))
            gameMapData.sections.forEach { (id, sectionData) ->
                sections[id] = MapSection(sectionData)
            }
            sections.forEach { (id, section) ->
                lengthPrefixSum[id] = (lengthPrefixSum[id - 1] ?: 0.0) + section.sectionLength
            }
            mapJsonFile.writeText(json.encodeToString(gameMapData))
        } catch (e: Exception) {
            e.printStackTrace()
            mapJsonFile.writeText("{}")
            loadMap()
        }
    }
}