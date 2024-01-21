package cn.xor7.map

import cn.xor7.getOrCreateJsonFile
import cn.xor7.readConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.concurrent.ConcurrentHashMap

@Suppress("MemberVisibilityCanBePrivate")
object GameMap {
    private const val MAP_DATA_FILE_NAME = ".nyan/map.json"
    internal val trackers = mutableMapOf<String, PlayerTracker>()
    internal val ranking = mutableListOf<PlayerTracker>()
    private val sections = ConcurrentHashMap<Int, MapSection>()
    private val json = Json { prettyPrint = true }
    private var lengthPrefixSum = mutableMapOf<Int, Double>()
    var showingMapParticle = false
    var showingRadiusParticle = false
    val playerSpawnInfo = mutableMapOf<String, Pair<Location, Int>>()

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

    fun sectionCount() = sections.size

    fun setSection(sectionId: Int, section: MapSection) {
        sections[sectionId] = section
        calcLengthPrefixSum()
        saveMap()
    }

    fun getLengthPrefixSum(sectionId: Int) = lengthPrefixSum[sectionId] ?: 0.0

    fun toggleMapParticle(broadcast: Boolean = true) {
        if (showingMapParticle) {
            sections.forEach { (_, section) ->
                section.mapParticle.turnOffTask()
            }
        } else {
            sections.forEach { (_, section) ->
                section.mapParticle.alwaysShowAsync()
            }
        }
        showingMapParticle = !showingMapParticle
        if (!broadcast) return
        Bukkit.broadcast(Component.text("§a已${if (showingMapParticle) "开启" else "关闭"}赛道路线指示粒子"))
    }

    fun toggleRadiusParticle(broadcast: Boolean = true) {
        if (showingRadiusParticle) {
            sections.forEach { (_, section) ->
                section.tunOffRadiusParticleTask()
            }
        } else {
            sections.forEach { (_, section) ->
                section.showRadiusParticle()
            }
        }
        showingRadiusParticle = !showingRadiusParticle
        if (!broadcast) return
        Bukkit.broadcast(Component.text("§a已${if (showingRadiusParticle) "开启" else "关闭"}赛道半径指示粒子"))
    }

    fun loadMap() {
        sections.clear()
        lengthPrefixSum.clear()

        readConfig<GameMapData>(MAP_DATA_FILE_NAME) { data ->
            data.sections.forEach { (id, sectionData) ->
                sections[id] = MapSection(sectionData)
            }
            calcLengthPrefixSum()
        }
    }

    private fun calcLengthPrefixSum() {
        val prefixSum = mutableMapOf<Int, Double>()
        sections.forEach { (id, section) ->
            prefixSum[id] = (prefixSum[id - 1] ?: 0.0) + section.sectionLength
        }
        lengthPrefixSum = prefixSum
    }

    fun saveMap() = getOrCreateJsonFile(MAP_DATA_FILE_NAME)
        .writeText(
            json.encodeToString(
                GameMapData(
                    sections.mapValues { (_, section) ->
                        section.getData()
                    }
                )
            )
        )

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