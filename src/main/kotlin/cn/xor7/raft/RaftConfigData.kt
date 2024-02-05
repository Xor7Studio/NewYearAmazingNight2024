package cn.xor7.raft

import kotlinx.serialization.Serializable

@Serializable
data class RaftConfigData(
    val allowedSections: Set<Int> = emptySet(),
)
