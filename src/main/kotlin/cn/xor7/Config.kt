package cn.xor7

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val json = Json { prettyPrint = true }

fun getOrCreateJsonFile(path: String):File {
    val jsonFile = File(path)
    if (!jsonFile.isFile) {
        jsonFile.createNewFile()
        jsonFile.writeText("{}")
    }
    return jsonFile
}

inline fun <reified T> readConfig(path: String, beforeWrite: (data: T) -> Unit = {}): T {
    val configJsonFile = File(path)
    if (!configJsonFile.isFile) {
        configJsonFile.createNewFile()
        configJsonFile.writeText("{}")
    }
    val data: T = json.decodeFromString<T>(Files.readString(Paths.get(path)))
    beforeWrite(data)
    println(data.toString())
    configJsonFile.writeText(json.encodeToString(data))
    return data
}
