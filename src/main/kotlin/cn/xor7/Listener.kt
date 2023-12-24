package cn.xor7

import cn.xor7.map.GameMap
import cn.xor7.map.PlayerTracker
import cn.xor7.scoreboard.ScoreboardManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class Listener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ScoreboardManager.setScoreboard(event.player)
        if(GameMap.trackers.containsKey(event.player.name)) return
        val tracker = PlayerTracker(event.player.name)
        GameMap.trackers[event.player.name] = tracker
        GameMap.ranking.add(tracker)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        ScoreboardManager.removeScoreboard(event.player)
    }
}