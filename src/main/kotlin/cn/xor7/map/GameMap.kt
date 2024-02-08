package cn.xor7.map

import cn.xor7.getOrCreateJsonFile
import cn.xor7.readConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.concurrent.CopyOnWriteArrayList

@Suppress("MemberVisibilityCanBePrivate")
object GameMap {
    private const val MAP_DATA_FILE_NAME = ".nyan/map.json"
    private val sections = CopyOnWriteArrayList<MapSection>()
    private val json = Json { prettyPrint = true }
    private var lengthPrefixSum = mutableMapOf<Int, Double>()
    val trackers = mutableMapOf<String, PlayerTracker>()
    val ranking = mutableListOf<PlayerTracker>()
    val mainTeam = Bukkit.getScoreboardManager().mainScoreboard.getTeam("main")
    var showingMapParticle = false
    var showingRadiusParticle = false
    val playerSpawnInfo = mutableMapOf<String, Pair<Location, Int>>()
    var gameRunning = false

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
        ranking.forEachIndexed { index, playerTracker ->
            playerTracker.ranking = index + 1
        }
    }

    fun getRankingList(index: Int, size: Int): List<String> {
        val halfWindowSize = size / 2
        val start = (index - halfWindowSize).coerceAtLeast(0)
        val end = (start + size).coerceAtMost(ranking.size)
        val result = ranking.subList(start, end).map { "${it.ranking} - ${it.playerName}" }.toMutableList()
        while (result.size < size) result.add("")
        return result
    }

    fun getSection(sectionId: Int): MapSection? = sections.getOrNull(sectionId)

    fun haveSection(sectionId: Int) = sections.size > sectionId

    fun sectionCount() = sections.size

    fun setSection(sectionId: Int, section: MapSection) {
        if (sectionId >= sections.size) sections.add(section)
        else sections[sectionId] = section
        calcLengthPrefixSum()
        saveMap()
    }

    fun insertSection(sectionId: Int, section: MapSection) {
        sections.add(sectionId, section)
        calcLengthPrefixSum()
        saveMap()
    }

    fun getLengthPrefixSum(sectionId: Int) = lengthPrefixSum[sectionId] ?: 0.0

    fun toggleMapParticle() {
        if (showingMapParticle) turnOffMapParticle() else turnOnMapParticle()
        showingMapParticle = !showingMapParticle
        Bukkit.broadcast(Component.text("§a已${if (showingMapParticle) "开启" else "关闭"}赛道路线指示粒子"))
    }

    fun turnOffMapParticle() = sections.forEach { it.mapParticle.turnOffTask() }

    fun turnOnMapParticle() = sections.forEach { it.mapParticle.alwaysShowAsync() }

    fun toggleRadiusParticle() {
        if (showingRadiusParticle) turnOffRadiusParticle() else turnOnRadiusParticle()
        showingRadiusParticle = !showingRadiusParticle
        Bukkit.broadcast(Component.text("§a已${if (showingRadiusParticle) "开启" else "关闭"}赛道半径指示粒子"))
    }

    fun turnOnRadiusParticle() = sections.forEach { it.showRadiusParticle() }

    fun turnOffRadiusParticle() = sections.forEach { it.tunOffRadiusParticleTask() }

    fun loadMap() {
        sections.clear()
        lengthPrefixSum.clear()

        readConfig<GameMapData>(MAP_DATA_FILE_NAME) { data ->
            data.sections.forEach { (id, sectionData) ->
                sections.add(id, MapSection(sectionData))
            }
            calcLengthPrefixSum()
        }
    }

    private fun calcLengthPrefixSum() {
        val prefixSum = mutableMapOf<Int, Double>()
        sections.forEachIndexed { index, mapSection ->
            prefixSum[index] = (prefixSum[index - 1] ?: 0.0) + mapSection.sectionLength
        }
        lengthPrefixSum = prefixSum
    }

    fun saveMap() = getOrCreateJsonFile(MAP_DATA_FILE_NAME)
        .writeText(
            json.encodeToString(
                GameMapData(
                    mutableMapOf<Int, MapSectionData>().apply {
                        sections.forEachIndexed { index, mapSection ->
                            this[index] = mapSection.getData()
                        }
                    }
                )
            )
        )

    fun MutableList<PlayerTracker>.insertionSort(compare: Comparator<PlayerTracker>) {
        for (i in 1 until size) {
            val key = this[i]
            var j = i - 1

            while (j >= 0 && compare.compare(key, this[j]) > 0) {
                this[j + 1] = this[j]
                j--
            }
            this[j + 1] = key
        }
    }

}