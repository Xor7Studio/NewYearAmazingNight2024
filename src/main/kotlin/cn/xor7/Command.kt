package cn.xor7

import cn.xor7.map.GameMap
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
            literalArgument("all"){
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
    }
}