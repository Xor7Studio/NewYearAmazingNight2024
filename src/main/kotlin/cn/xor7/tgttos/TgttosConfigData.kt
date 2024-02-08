package cn.xor7.tgttos

import kotlinx.serialization.Serializable

@Serializable
data class TgttosConfigData(
    val allowedSections: Set<Int> = emptySet(),
)
