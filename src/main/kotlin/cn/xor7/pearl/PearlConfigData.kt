package cn.xor7.pearl

import kotlinx.serialization.Serializable

@Serializable
data class PearlConfigData(
    // 这俩东西没做出来呢...
    // val disableSectionRadiusCheckWhenPearlFlying: Boolean = true,
    // val clearTransgressivePearls: Boolean = true,
    val allowedSections: Set<Int> = emptySet(),
)
