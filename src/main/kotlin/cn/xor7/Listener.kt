package cn.xor7

import cn.xor7.map.GameMap
import cn.xor7.map.PlayerTracker
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Listener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendMessage("Hello, ${event.player.name}!")
        if(GameMap.trackers.containsKey(event.player.name)) return
        val tracker = PlayerTracker(event.player.name)
        GameMap.trackers[event.player.name] = tracker
        GameMap.ranking.add(tracker)
    }
}