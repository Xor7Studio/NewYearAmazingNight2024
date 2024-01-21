package cn.xor7.pearl

import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

object PearlListener : Listener {
    @EventHandler
    fun onPearlFly(event: EntityMoveEvent) {
        if (event.entity.type != EntityType.ENDER_PEARL) return
        println(event.to)
        if(!PearlManager.inAllowedLocation(event.to)) event.entity.remove()
    }

    @EventHandler
    fun onPearlLand(event: PlayerTeleportEvent) {
        if (event.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return
        if (!PearlManager.inAllowedLocation(event.to)) event.isCancelled = true
    }
}