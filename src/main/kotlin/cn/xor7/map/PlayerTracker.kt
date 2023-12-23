package cn.xor7.map

import org.bukkit.Bukkit

@Suppress("MemberVisibilityCanBePrivate")
class PlayerTracker internal constructor(val playerName: String) {
    var nowSectionId: Int = 0
        private set
    var nowPosition: Double = 0.0
        private set

    fun trackNowSection() {
        val player = Bukkit.getPlayer(playerName) ?: return

        val nowSection: MapSection = GameMap.sections[nowSectionId] ?: run {
            nowSectionId = 0
            GameMap.sections[0]!!
        }

        var minDistanceSquared = nowSection.getDistanceSquared(player.location)
        var nowSectionPosition = nowSection.getPosition(player.location)
        for (i in nowSectionId - 2..nowSectionId + 2) {
            if (i == nowSectionId) continue
            val section = GameMap.sections[i] ?: continue
            val position = section.getPosition(player.location)
            val distanceSquared = section.getDistanceSquared(player.location)
            if (distanceSquared < minDistanceSquared) {
                nowSectionId = i
                minDistanceSquared = distanceSquared
                nowSectionPosition = position
            }
        }

        nowPosition = GameMap.getLengthPrefixSum(nowSectionId - 1) + nowSectionPosition
    }
}