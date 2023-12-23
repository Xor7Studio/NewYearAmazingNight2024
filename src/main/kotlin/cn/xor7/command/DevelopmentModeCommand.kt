package cn.xor7.command

import cn.xor7.toggleDevelopmentMode
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandPermission("nyan.development")
@Suppress("MemberVisibilityCanBePrivate", "unused")
@CommandAlias("development|dev")
class DevelopmentModeCommand : BaseCommand() {
    @Default
    @CommandCompletion("@players")
    fun onCommand(sender: CommandSender, @Optional player: OnlinePlayer?) {
        if (sender !is Player) {
            if (player == null) {
                sender.sendMessage("§c终端执行该命令时必须指定玩家！")
                return
            }
            sender.sendMessage(
                "§a已为玩家 ${player.player.name} ${
                    if (player.player.toggleDevelopmentMode()) "开启" else "关闭"
                }开发者模式"
            )
            return
        }
        (player?.player ?: sender).toggleDevelopmentMode()
    }
}