package cn.xor7.gravel

import kotlinx.serialization.Serializable

@Serializable
data class GravelConfigData(
    val targetPlayer: String = "MC_XiaoHei",
    val targetSection: Int = 0,
)
