package cn.xor7.map

import cn.xor7.*
import cn.xor7.gravel.GravelManager
import cn.xor7.gravel.GravelManager.canPassGravelSection
import cn.xor7.raft.RaftManager
import cn.xor7.scoreboard.ScoreboardManager
import cn.xor7.spectator.SpectatorManager.isSpectator
import cn.xor7.tgttos.TgttosManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffect.INFINITE_DURATION
import org.bukkit.potion.PotionEffectType


object MapListener : Listener {
    private val showOtherPlayer = mutableMapOf<String, Boolean>()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if(player.isSpectator) return
        ScoreboardManager.setScoreboard(player)
        if (GameMap.trackers.containsKey(player.name)) return
        player.teleport(player.world.spawnLocation)
        val tracker = PlayerTracker(player.name)
        GameMap.trackers[player.name] = tracker
        GameMap.ranking.add(tracker)
        GameMap.mainTeam?.addEntity(player)
        player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, INFINITE_DURATION, 0, true, false))
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

        if (TgttosManager.allowedSectionsContains(tracker.nowSectionId)) {
            player.inventory.setItemInOffHand(ItemStack(Material.LIME_WOOL, 64))
            player.inventory.chestplate = ItemStack(Material.ELYTRA)
        } else {
            player.inventory.setItemInOffHand(ItemStack(Material.AIR))
            player.inventory.chestplate = ItemStack(Material.AIR)
            player.inventory.forEach {
                val type = it?.type ?: return@forEach
                if (type == Material.LIME_WOOL || type == Material.ELYTRA)
                    player.inventory.remove(it)
            }
        }

        when (block.type) {
            Material.GOLD_BLOCK -> {
                player.setBedSpawnLocation(player.location, true)
                GameMap.playerSpawnInfo[tracker.playerName] = Pair(player.location, tracker.nowSectionId)
                player.sendActionBar(Component.text("§b§l已设置重生点"))
            }

            Material.GRAVEL -> {
                if (GravelManager.getTargetSectionId() != tracker.nowSectionId) return
                if (!player.canPassGravelSection()) player.sendMessage("§c在玩家 ${GravelManager.getTargetPlayerName()} 通过本关前，你不能进行尝试！")
                player.sendToSpawnPoint()
            }

            Material.SUSPICIOUS_GRAVEL -> {
                if (GravelManager.getTargetSectionId() != tracker.nowSectionId) return
                if (!player.canPassGravelSection()) {
                    player.sendMessage("§c在玩家 ${GravelManager.getTargetPlayerName()} 通过本关前，你不能进行尝试！")
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
                        Material.LIME_WOOL -> return
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
                event.cancelWhenPlaying(player)
            }

            LEFT_CLICK_BLOCK -> {
                val block = event.clickedBlock ?: return
                if (block.type == Material.LIME_WOOL) return
                event.cancelWhenPlaying(player)
            }

            LEFT_CLICK_AIR -> event.cancelWhenPlaying(player)
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

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (entity is Player)
            if (!TgttosManager.allowedSectionsContains(entity.tracker?.nowSectionId ?: return))
                event.isCancelled = true
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) = event.cancelWhenPlaying(event.player)

    @EventHandler
    fun onClickItem(event: InventoryClickEvent) = event.cancelWhenPlaying(event.whoClicked as Player)
}