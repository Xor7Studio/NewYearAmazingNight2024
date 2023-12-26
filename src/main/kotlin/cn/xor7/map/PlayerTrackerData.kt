package cn.xor7.map

import kotlinx.serialization.Serializable

@Serializable
data class PlayerTrackerData(
    val name: String,
    val location: SimpleLocation,
    val position: Double,
)
