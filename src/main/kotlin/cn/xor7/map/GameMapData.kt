package cn.xor7.map

import kotlinx.serialization.Serializable

@Serializable
data class GameMapData(
    val sections: Map<Int, MapSectionData> = mutableMapOf(),
)
