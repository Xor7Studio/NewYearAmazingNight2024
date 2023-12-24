package cn.xor7.scoreboard

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object ScoreboardManager {
    internal lateinit var plugin: JavaPlugin
    private const val SCOREBOARD_DATA_CONFIG_FILE_NAME = ".nyan/scoreboard.json"
    private lateinit var scoreboardData: ScoreboardData
    private val json = Json { prettyPrint = true }

    init {
        try {
            loadScoreboardData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadScoreboardData() {
        val scoreboardDataJsonFile = File(SCOREBOARD_DATA_CONFIG_FILE_NAME)
        if (!scoreboardDataJsonFile.isFile) {
            scoreboardDataJsonFile.createNewFile()
            scoreboardDataJsonFile.writeText("{}")
        }
        try {
            scoreboardData =
                json.decodeFromString<ScoreboardData>(Files.readString(Paths.get(SCOREBOARD_DATA_CONFIG_FILE_NAME)))
            scoreboardDataJsonFile.writeText(json.encodeToString(scoreboardData))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}