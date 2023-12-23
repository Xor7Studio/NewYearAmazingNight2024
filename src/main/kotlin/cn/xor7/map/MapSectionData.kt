package cn.xor7.map

import kotlinx.serialization.Serializable

@Serializable
data class MapSectionData(
    val beginPos: SimpleLocation,
    val endPos: SimpleLocation,
)
