package cn.xor7.map

import cn.xor7.getTracker
import cn.xor7.gravel.GravelManager
import cn.xor7.gravel.GravelManager.canPassGravelSection
import cn.xor7.instance
import cn.xor7.scoreboard.ScoreboardManager
import cn.xor7.sendToSpawnPoint
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

object MapListener : Listener {
    private val hideOtherPlayer = mutableMapOf<String, Boolean>()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        ScoreboardManager.setScoreboard(player)
        if (GameMap.trackers.containsKey(player.name)) return
        player.teleport(player.world.spawnLocation)
        val tracker = PlayerTracker(player.name)
        GameMap.trackers[player.name] = tracker
        GameMap.ranking.add(tracker)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) = ScoreboardManager.removeScoreboard(event.player)

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val tracker = player.getTracker() ?: return
        val blockPos = event.to.toBlockLocation().subtract(0.0, 1.0, 0.0)
        val block = blockPos.block

        if (player.name == GravelManager.getTargetPlayerName() &&
            tracker.nowSectionId > GravelManager.getTargetSectionId()
        ) GravelManager.pass = true

        when (block.type) {
            Material.GOLD_BLOCK -> {
                player.setBedSpawnLocation(player.location, true)
                GameMap.playerSpawnInfo[tracker.playerName] = Pair(player.location, tracker.nowSectionId)
                player.sendActionBar(Component.text("§b§l已设置重生点"))
            }

            Material.GRAVEL -> {
                if (GravelManager.getTargetSectionId() != tracker.nowSectionId) return
                player.sendToSpawnPoint()
            }

            Material.SUSPICIOUS_GRAVEL -> {
                if (GravelManager.getTargetSectionId() != tracker.nowSectionId) return
                if (!player.canPassGravelSection()) player.sendToSpawnPoint()
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
            Material.ENDER_EYE -> {
                if (hideOtherPlayer[player.name] == false) {
                    hideOtherPlayer[player.name] = true
                    Bukkit.getOnlinePlayers().forEach { player.hidePlayer(instance, it) }
                    player.sendMessage("§a已隐藏其他玩家")
                } else {
                    hideOtherPlayer[player.name] = false
                    Bukkit.getOnlinePlayers().forEach { player.showPlayer(instance, it) }
                    player.sendMessage("§a已显示其他玩家")
                }
            }

            else -> return
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity !is org.bukkit.entity.Player) return
        val player = event.entity as org.bukkit.entity.Player
        if (player.getTracker()?.invincible == true) event.isCancelled = true
    }
}