package cn.xor7

import cn.xor7.map.GameMap
import cn.xor7.map.PlayerTracker
import cn.xor7.scoreboard.ScoreboardManager
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

object Listener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ScoreboardManager.setScoreboard(event.player)
        if (GameMap.trackers.containsKey(event.player.name)) return
        val tracker = PlayerTracker(event.player.name)
        GameMap.trackers[event.player.name] = tracker
        GameMap.ranking.add(tracker)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) = ScoreboardManager.removeScoreboard(event.player)

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val tracker = GameMap.trackers[event.player.name] ?: return
        val blockPos = event.to.toBlockLocation().subtract(0.0, 1.0, 0.0)
        val block = blockPos.block
        when (block.type) {
            Material.GOLD_BLOCK -> {
                GameMap.playerSpawnInfo[tracker.playerName] = Pair(event.player.location, tracker.nowSectionId)
                event.player.sendActionBar(Component.text("§b§l已设置重生点"))
            }

            else -> return
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val player = event.player
        when (item.type) {
            Material.BLAZE_ROD -> player.sendToSpawnPoint()

            else -> {
                // Other logic
                return
            }
        }
        event.isCancelled = true
    }
}