package cn.xor7.map

import cn.xor7.pearl.PearlManager
import cn.xor7.raft.RaftManager
import cn.xor7.removeBambooRaft
import cn.xor7.removeEnderPearl
import cn.xor7.sendToSpawnPoint
import cn.xor7.toOrdinal
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.util.Ticks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Suppress("MemberVisibilityCanBePrivate")
class PlayerTracker internal constructor(val playerName: String) {
    var nowSectionId = 0
        internal set(newValue) {
            field = newValue
            nowSection = GameMap.getSection(newValue)!!
        }

    var nowSection = GameMap.getSection(0)!!
        private set
    var nowPosition = 0.0
        private set
    var nowSectionPosition = 0.0
        private set
    var nowSectionDistanceSquared = 0.0
        private set

    var developmentMode = false
    var over = false
    var nowLocation = SimpleLocation(0.0, 0.0, 0.0)
    var ranking = 1

    @Volatile
    var invincible = false

    fun trackNowSection() {
        if (over) return

        val player = Bukkit.getPlayer(playerName) ?: return
        nowLocation = SimpleLocation(player.location.x, player.location.y, player.location.z)

        if (!GameMap.haveSection(nowSectionId)) nowSectionId = 0

        var nowSectionPosition = nowSection.getPosition(player.location)
        var nowDistanceToBeginPointSquared = nowSection.getDistanceToBeginPointSquared(player.location)
        var minDistanceSquared = nowSection.getDistanceSquared(nowDistanceToBeginPointSquared, nowSectionPosition)
        for (i in nowSectionId - 5..nowSectionId + 5) {
            val section = GameMap.getSection(i) ?: continue
            val position = section.getPosition(player.location)
            val distanceToBeginPointSquared = section.getDistanceToBeginPointSquared(player.location)
            val distanceSquared = section.getDistanceSquared(distanceToBeginPointSquared, position)
            if (i == 0 && position < 0) {
                nowSectionId = 0
                this.nowPosition = 0.0
                this.nowSectionPosition = 0.0
                this.nowSectionDistanceSquared = 0.0
                return
            }
            if (distanceSquared < minDistanceSquared && position in 0.0..section.sectionLength) {
                nowSectionId = i
                nowDistanceToBeginPointSquared = distanceToBeginPointSquared
                minDistanceSquared = distanceSquared
                nowSectionPosition = position
            }
        }

        if (!developmentMode) {
            if (!GameMap.gameRunning && nowSectionPosition > 0) {
                player.sendMessage("§c游戏尚未开始！")
                player.sendToSpawnPoint()
                nowPosition = 0.0
                this.nowSectionPosition = 0.0
                this.nowSectionDistanceSquared = 0.0
                return
            }
            if (nowSectionId == (GameMap.sectionCount() - 1) && nowSectionPosition >= (nowSection.sectionLength - 1)) {
                nowPosition = GameMap.getLengthPrefixSum(nowSectionId) + 2024 - ranking
                nowSectionId = 0
                over = true
                player.showTitle(
                    Title.title(
                        Component.text("§a恭喜你完成比赛！"),
                        Component.text(ranking.toOrdinal()),
                        Title.Times.times(
                            Ticks.duration(10L),
                            Ticks.duration(300L),
                            Ticks.duration(20L)
                        )
                    )
                )
                player.teleport(player.world.spawnLocation)
                return
            }
            if (minDistanceSquared > nowSection.radiusSquared ||
                (nowSectionPosition < 0 && nowDistanceToBeginPointSquared > nowSection.radiusSquared)
            ) {
                nowSectionId = GameMap.playerSpawnInfo[playerName]?.second ?: 0
                player.sendToSpawnPoint()
            }
        }

        nowSectionPosition = when {
            nowSectionPosition > nowSection.sectionLength -> nowSection.sectionLength
            nowSectionPosition < 0 -> 0.0
            else -> nowSectionPosition
        }

        this.nowSectionPosition = nowSectionPosition
        nowSectionDistanceSquared = minDistanceSquared

        nowPosition = GameMap.getLengthPrefixSum(nowSectionId - 1) + nowSectionPosition

        if (PearlManager.allowedSectionsContains(nowSection)) {
            player.removeEnderPearl()
            player.inventory.addItem(ItemStack(Material.ENDER_PEARL).apply {
                itemMeta = itemMeta.apply {
                    displayName(Component.text("§a传送珍珠"))
                    isUnbreakable = true
                }
            })
        } else player.removeEnderPearl()

        if (RaftManager.allowedSectionsContains(nowSectionId)) {
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SLOW,
                    20,
                    3,
                    true,
                    false,
                    false
                )
            )
            player.removeBambooRaft()
            if (player.vehicle == null) player.inventory.addItem(ItemStack(Material.BAMBOO_RAFT).apply {
                itemMeta = itemMeta.apply {
                    displayName(Component.text("§a木筏"))
                    isUnbreakable = true
                }
            })
        } else player.removeBambooRaft()
    }
}