package cn.xor7.scoreboard

import cn.xor7.readConfig
import cn.xor7.tracker
import fr.mrmicky.fastboard.FastBoard
import org.bukkit.entity.Player

object ScoreboardManager {
    private const val SCOREBOARD_DATA_CONFIG_FILE_NAME = ".nyan/scoreboard.json"
    private lateinit var scoreboardData: ScoreboardData
    private val scoreboards = mutableMapOf<Player, FastBoard>()

    init {
        try {
            scoreboardData = readConfig(SCOREBOARD_DATA_CONFIG_FILE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateScoreboard() {
        scoreboards.forEach { (player, scoreboard) ->
            scoreboard.updateLines(run {
                if (player.tracker!!.developmentMode)
                    scoreboardData.devLines
                else
                    scoreboardData.lines
            }.map { line ->
                val tracker = player.tracker!!
                line.replace("%player%", player.name)
                    .replace("%section%", tracker.nowSectionId.toString())
                    .replace("%position%", tracker.nowPosition.toString())
                    .replace("%section_position%", tracker.nowSectionPosition.toString())
                    .replace("%section_distance%", tracker.nowSectionDistanceSquared.toString())
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
}