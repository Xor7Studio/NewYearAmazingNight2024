package cn.xor7.map

import kotlinx.serialization.Serializable

@Serializable
data class SimpleLocation(
    val x: Double,
    val y: Double,
    val z: Double,
)
