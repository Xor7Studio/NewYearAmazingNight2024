package cn.xor7.spectator

import kotlinx.serialization.Serializable

@Serializable
data class SpectatorConfigData(
    val spectators: Set<String> = emptySet(),
)
