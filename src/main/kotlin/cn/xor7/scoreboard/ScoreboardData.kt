package cn.xor7.scoreboard

import kotlinx.serialization.Serializable

@Serializable
data class ScoreboardData(
    val title: String,
    val rankLineNum: Int,
    val lines: List<String>,
    val devLines: List<String>,
)
