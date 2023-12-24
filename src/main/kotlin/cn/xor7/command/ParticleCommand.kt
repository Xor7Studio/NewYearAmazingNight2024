package cn.xor7.command

import cn.xor7.map.GameMap
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand

@CommandPermission("nyan.particle")
@Suppress("MemberVisibilityCanBePrivate", "unused")
@CommandAlias("toggle-particle|p")
class ParticleCommand : BaseCommand() {
    @Default
    @Subcommand("all")
    fun toggleAllParticles() {
        GameMap.toggleMapParticle()
    }
}