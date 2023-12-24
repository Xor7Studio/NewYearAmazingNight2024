package cn.xor7.map

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
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
    var showingMapParticle = false

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
        ranking.insertionSort(compareBy({ it.nowPosition }, { it.playerName }))
    }

    fun getRanking() = ranking.toList()

    fun getSection(sectionId: Int) = sections[sectionId]

    fun haveSection(sectionId: Int) = sections.containsKey(sectionId)

    fun getLengthPrefixSum(sectionId: Int) = lengthPrefixSum[sectionId] ?: 0.0

    fun toggleMapParticle() {
        if (showingMapParticle) {
            sections.forEach { (_, section) ->
                section.particle.turnOffTask()
            }
        } else {
            sections.forEach { (_, section) ->
                section.particle.alwaysShowAsync()
            }
        }
        showingMapParticle = !showingMapParticle
        Bukkit.broadcast(Component.text("§a已${if (showingMapParticle) "开启" else "关闭"}赛道指示粒子"))
    }

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

    fun MutableList<PlayerTracker>.insertionSort(compare: Comparator<PlayerTracker>) {
        for (i in 1 until size) {
            val key = this[i]
            var j = i - 1

            while (j >= 0 && compare.compare(this[j], key) > 0) {
                this[j + 1] = this[j]
                j--
            }
            this[j + 1] = key
        }
    }
}