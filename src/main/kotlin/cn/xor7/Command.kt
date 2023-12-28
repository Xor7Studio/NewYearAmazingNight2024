package cn.xor7

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
    }
}