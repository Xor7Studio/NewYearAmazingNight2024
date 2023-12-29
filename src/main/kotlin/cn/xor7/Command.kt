package cn.xor7

import cn.xor7.map.GameMap
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.entity.Player

object Command {
    fun register() {
        commandTree("dev") {
            playerArgument("player", optional = true) {
                playerExecutor { sender, args ->
                    (args.getOptional("player").orElse(sender) as Player).toggleDevelopmentMode()
                }
            }
        }
        commandTree("p") {
            anyExecutor { _, _ ->
                GameMap.toggleMapParticle()
                GameMap.toggleRadiusParticle()
            }
            commandTree("map") {
                anyExecutor { _, _ ->
                    GameMap.toggleMapParticle()
                }
            }
            commandTree("radius") {
                anyExecutor { _, _ ->
                    GameMap.toggleRadiusParticle()
                }
            }
        }
    }
}