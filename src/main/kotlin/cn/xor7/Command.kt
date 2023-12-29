package cn.xor7

import cn.xor7.map.GameMap
import cn.xor7.map.MapSection
import dev.jorel.commandapi.kotlindsl.*
import org.bukkit.entity.Player

object Command {
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
                                commandExecutor.sendMessage("§c赛道不存在")
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
        }
    }
}