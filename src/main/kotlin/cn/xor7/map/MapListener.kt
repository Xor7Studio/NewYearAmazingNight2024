package cn.xor7.map

import cn.xor7.*
import cn.xor7.gravel.GravelManager
import cn.xor7.gravel.GravelManager.canPassGravelSection
import cn.xor7.raft.RaftManager
import cn.xor7.scoreboard.ScoreboardManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.*
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.vehicle.VehicleExitEvent

object MapListener : Listener {
    private val showOtherPlayer = mutableMapOf<String, Boolean>()

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
        val tracker = player.tracker ?: return
        val blockPos = event.to.toBlockLocation().subtract(0.0, 1.0, 0.0)
        val block = blockPos.block

        if (player.name == GravelManager.getTargetPlayerName() &&
            tracker.nowSectionId > GravelManager.getTargetSectionId()
        ) GravelManager.pass = true

        if (!RaftManager.allowedSectionsContains(tracker.nowSectionId) && player.vehicle?.type == EntityType.BOAT)
            player.vehicle?.remove()

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
                if (!player.canPassGravelSection()) {
                    player.sendMessage("§c在玩家${GravelManager.getTargetPlayerName()}通过本关前，你不能进行尝试！")
                    player.sendToSpawnPoint()
                }
            }

            else -> return
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        when (event.action) {
            RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                run {
                    val item = event.item ?: return@run
                    when (item.type) {
                        Material.BLAZE_ROD -> player.sendToSpawnPoint()
                        Material.ENDER_PEARL -> return
                        Material.ENDER_EYE -> {
                            if (showOtherPlayer[player.name] == false) {
                                showOtherPlayer[player.name] = true
                                Bukkit.getOnlinePlayers().forEach { player.showPlayer(instance, it) }
                                player.sendMessage("§a已显示其他玩家")
                            } else {
                                showOtherPlayer[player.name] = false
                                Bukkit.getOnlinePlayers().forEach { player.hidePlayer(instance, it) }
                                player.sendMessage("§a已隐藏其他玩家")
                            }
                        }

                        Material.BAMBOO_RAFT -> {
                            player.vehicle?.remove()
                            player.removeBambooRaft()
                            player.world.spawn(player.location, Boat::class.java).apply {
                                boatType = Boat.Type.BAMBOO
                                addPassenger(player)
                            }
                        }

                        else -> return@run
                    }
                }
                event.cancelInNonDevMode(player)
            }

            LEFT_CLICK_BLOCK -> event.cancelInNonDevMode(player)
            LEFT_CLICK_AIR -> event.cancelInNonDevMode(player)
            PHYSICAL -> if (event.clickedBlock?.type == Material.FARMLAND) event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (player.tracker?.invincible == true) event.isCancelled = true
    }

    @EventHandler
    fun onVehicleExit(event: VehicleExitEvent) {
        val exited = event.exited
        if (exited is Player) {
            exited.removeBambooRaft()
            event.vehicle.remove()
        }
    }
}