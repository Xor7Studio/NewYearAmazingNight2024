package cn.xor7.pearl

import kotlinx.serialization.Serializable

@Serializable
data class PearlConfigData(
    val disableSectionRadiusCheckWhenPearlFlying: Boolean = true,
    val clearTransgressivePearls: Boolean = true,
    val allowedSections: Set<Int> = emptySet(),
)
