package cn.xor7.item

import org.bukkit.entity.Player

interface FunctionalItem {
    fun onUse(player: Player)

    fun onAttack(player: Player)
}