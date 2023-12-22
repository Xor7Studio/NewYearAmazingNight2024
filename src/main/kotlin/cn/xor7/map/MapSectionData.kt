package cn.xor7.map

import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
data class MapSectionData(
    val beginPos: SimpleLocation,
    val endPos: SimpleLocation,
    val block: Boolean,
)
