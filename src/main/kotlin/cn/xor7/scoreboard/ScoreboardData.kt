package cn.xor7.scoreboard

import kotlinx.serialization.Serializable

@Serializable
data class ScoreboardData(
    val title: String = "",
    val rankLineNum: Int = 0,
    val lines: List<String> = emptyList(),
    val devLines: List<String> = emptyList(),
)
