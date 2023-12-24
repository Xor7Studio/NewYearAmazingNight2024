package cn.xor7.map

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location

@Serializable
data class SimpleLocation(
    val x: Double,
    val y: Double,
    val z: Double,
) {
    fun toLocation() = Location(Bukkit.getWorlds()[0], x, y, z)
}