package cn.xor7.map

import cn.xor7.sendToSpawnPoint
import org.bukkit.Bukkit

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
    var nowLocation = SimpleLocation(0.0, 0.0, 0.0)

    @Volatile
    var invincible = false

    fun trackNowSection() {
        val player = Bukkit.getPlayer(playerName) ?: return
        nowLocation = SimpleLocation(player.location.x, player.location.y, player.location.z)

        if (!GameMap.haveSection(nowSectionId)) nowSectionId = 0


        var nowSectionPosition = nowSection.getPosition(player.location)
        var nowDistanceToBeginPointSquared = nowSection.getDistanceToBeginPointSquared(player.location)
        var minDistanceSquared = nowSection.getDistanceSquared(nowDistanceToBeginPointSquared, nowSectionPosition)
        for (i in nowSectionId - 2..nowSectionId + 2) {
            if (i == nowSectionId) continue
            val section = GameMap.getSection(i) ?: continue
            val position = section.getPosition(player.location)
            val distanceToBeginPointSquared = section.getDistanceToBeginPointSquared(player.location)
            val distanceSquared = section.getDistanceSquared(distanceToBeginPointSquared, position)
            if (distanceSquared < minDistanceSquared && position > -1) {
                nowSectionId = i
                nowDistanceToBeginPointSquared = distanceToBeginPointSquared
                minDistanceSquared = distanceSquared
                nowSectionPosition = position
            }
        }

        if (!developmentMode &&
            (minDistanceSquared > nowSection.radiusSquared
                    || (nowSectionPosition < 0 && nowDistanceToBeginPointSquared > nowSection.radiusSquared))
        ) {
            nowSectionId = GameMap.playerSpawnInfo[playerName]?.second ?: 0
            player.sendToSpawnPoint()
        }

        nowSectionPosition = when {
            nowSectionPosition > nowSection.sectionLength -> nowSection.sectionLength
            nowSectionPosition < 0 -> 0.0
            else -> nowSectionPosition
        }

        this.nowSectionPosition = nowSectionPosition
        nowSectionDistanceSquared = minDistanceSquared

        nowPosition = GameMap.getLengthPrefixSum(nowSectionId - 1) + nowSectionPosition
    }

    fun getData(): PlayerTrackerData {
        return PlayerTrackerData(
            name = playerName,
            location = nowLocation,
            position = nowPosition,
        )
    }
}