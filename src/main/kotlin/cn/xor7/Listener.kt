package cn.xor7

import cn.xor7.map.GameMap
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Listener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent){
        event.player.sendMessage("Hello, ${event.player.name}!")
        if(!GameMap.getTrackers().containsKey(event.player.name))
            GameMap.createTracker(event.player)
    }
}