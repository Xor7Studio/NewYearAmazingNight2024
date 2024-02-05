package cn.xor7.raft

import cn.xor7.readConfig

object RaftManager {
    private const val RAFT_CONFIG_FILE_NAME = ".nyan/raft-conf.json"
    private lateinit var configData: RaftConfigData

    private val allowedSections = mutableSetOf<Int>()

    init {
        try {
            configData = readConfig(RAFT_CONFIG_FILE_NAME)
            allowedSections.addAll(configData.allowedSections)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun allowedSectionsContains(section: Int) = allowedSections.contains(section)
}