package cn.xor7.scoreboard

import fr.mrmicky.fastboard.FastBoard
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object ScoreboardManager {
    private const val SCOREBOARD_DATA_CONFIG_FILE_NAME = ".nyan/scoreboard.json"
    private lateinit var scoreboardData: ScoreboardData
    private val json = Json { prettyPrint = true }
    private val scoreboards = mutableMapOf<Player, FastBoard>()

    init {
        try {
            loadScoreboardData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateScoreboard() {
        scoreboards.forEach { (player, scoreboard) ->
            scoreboard.updateLines(scoreboardData.lines.map { line ->
                line.replace("%player%", player.name)
            })
        }
    }

    fun setScoreboard(player: Player) {
        val scoreboard = FastBoard(player)
        scoreboard.updateTitle(scoreboardData.title)
        scoreboards[player] = scoreboard
    }

    fun removeScoreboard(player: Player) {
        scoreboards.remove(player)
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