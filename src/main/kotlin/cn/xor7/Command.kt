package cn.xor7

import cn.xor7.map.GameMap
import cn.xor7.map.MapSection
import cn.xor7.map.toSimpleLocation
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.entity.Player

object Command {
    @Suppress("DuplicatedCode")
    fun register() {
        commandTree("development") {
            withAliases("dev")
            playerArgument("player", optional = true) {
                playerExecutor { sender, args ->
                    (args.getOptional("player").orElse(sender) as Player).toggleDevelopmentMode()
                }
            }
        }
        commandTree("toggle-particle") {
            withAliases("p")
            literalArgument("all") {
                anyExecutor { _, _ ->
                    GameMap.toggleMapParticle()
                    GameMap.toggleRadiusParticle()
                }
            }
            literalArgument("map") {
                anyExecutor { _, _ ->
                    GameMap.toggleMapParticle()
                }
            }
            literalArgument("radius") {
                anyExecutor { _, _ ->
                    GameMap.toggleRadiusParticle()
                }
            }
        }
        commandTree("edit") {
            literalArgument("radius") {
                integerArgument("section") {
                    integerArgument("radius") {
                        anyExecutor { commandExecutor, commandArguments ->
                            val sectionId = commandArguments["section"] as Int
                            val section = GameMap.getSection(sectionId) ?: run {
                                commandExecutor.sendMessage("§c赛段 $sectionId 不存在")
                                return@anyExecutor
                            }
                            val radius = (commandArguments["radius"] as Int).toDouble()
                            GameMap.toggleRadiusParticle(false)
                            GameMap.setSection(sectionId, MapSection(section.getData().copy(radius = radius)))
                            commandExecutor.sendMessage("§a已将赛段 $sectionId 的半径设置为 $radius")
                            GameMap.toggleRadiusParticle(false)
                        }
                    }
                }
            }
            literalArgument("point") {
                integerArgument("point") {
                    locationArgument("location") {
                        anyExecutor { commandExecutor, commandArguments ->
                            val secondSectionId = commandArguments["point"] as Int
                            val firstSectionId = secondSectionId - 1
                            val location = (commandArguments["location"] as org.bukkit.Location).toSimpleLocation()
                            println(commandArguments["location"] as org.bukkit.Location)
                            println(location)
                            GameMap.toggleMapParticle(false)
                            GameMap.toggleRadiusParticle(false)
                            if (firstSectionId >= 0) {
                                val firstSection = GameMap.getSection(firstSectionId) ?: run {
                                    commandExecutor.sendMessage("§c赛段 $firstSectionId 不存在")
                                    GameMap.toggleMapParticle(false)
                                    GameMap.toggleRadiusParticle(false)
                                    return@anyExecutor
                                }
                                firstSection.tunOffRadiusParticleTask()
                                GameMap.setSection(firstSectionId, MapSection(firstSection.getData().copy(endPos = location)))
                            }
                            val secondSection = GameMap.getSection(secondSectionId) ?: run {
                                commandExecutor.sendMessage("§c赛段 $secondSectionId 不存在")
                                GameMap.toggleMapParticle(false)
                                GameMap.toggleRadiusParticle(false)
                                return@anyExecutor
                            }
                            secondSection.tunOffRadiusParticleTask()
                            GameMap.setSection(secondSectionId, MapSection(secondSection.getData().copy(beginPos = location)))
                            GameMap.toggleMapParticle(false)
                            GameMap.toggleRadiusParticle(false)
                            commandExecutor.sendMessage("§a已将赛道关键点 $secondSectionId 设置为 (x: ${location.x}, y: ${location.y}, z: ${location.z})")
                        }
                    }
                }
            }
        }
    }
}