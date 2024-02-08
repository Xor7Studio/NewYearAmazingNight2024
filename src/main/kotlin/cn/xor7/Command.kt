package cn.xor7

import cn.xor7.gravel.GravelManager
import cn.xor7.map.*
import dev.jorel.commandapi.kotlindsl.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Command {
    fun register() {
        commandTree("start") {
            anyExecutor { _, _ ->
                Bukkit.broadcast(Component.text("§a游戏开始！"))
                Bukkit.getOnlinePlayers().forEach {
                    it.sendToSpawnPoint()
                    it.inventory.apply {
                        setItem(0,ItemStack(Material.BLAZE_ROD).apply {
                            itemMeta = itemMeta.apply {
                                displayName(Component.text("§a返回记录点"))
                            }
                        })
                        setItem(1,ItemStack(Material.ENDER_EYE).apply {
                            itemMeta = itemMeta.apply {
                                displayName(Component.text("§a切换玩家可见性"))
                            }
                        })
                    }
                }
                GameMap.gameRunning = true
            }
        }
        commandTree("state") {
            withPermission("nyan.state")
            literalArgument("gravel") {
                booleanArgument("state") {
                    anyExecutor { _, args ->
                        GravelManager.pass = args.get("state") as Boolean
                    }
                }
            }
            literalArgument("section") {
                integerArgument("id") {
                    playerExecutor { player, commandArguments ->
                        player.tracker?.nowSectionId = commandArguments["id"] as Int
                    }
                }
            }
            literalArgument("running") {
                booleanArgument("state") {
                    playerExecutor { _, commandArguments ->
                        GameMap.gameRunning = commandArguments["state"] as Boolean
                    }
                }
            }
        }
        commandTree("development") {
            withPermission("nyan.dev")
            withAliases("dev")
            playerArgument("player", optional = true) {
                playerExecutor { sender, args ->
                    val player = args.getOptional("player").orElse(sender) as Player
                    if (player.tracker!!.developmentMode) {
                        player.tracker!!.developmentMode = false
                        player.sendMessage("§a已关闭开发者模式")
                    } else {
                        player.tracker!!.developmentMode = true
                        player.sendMessage("§a已开启开发者模式")
                    }
                }
            }
        }
        commandTree("toggle-particle") {
            withPermission("nyan.particle")
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
            withPermission("nyan.edit")
            literalArgument("radius") {
                integerArgument("section") {
                    doubleArgument("radius") {
                        anyExecutor { commandExecutor, commandArguments ->
                            val sectionId = commandArguments["section"] as Int
                            val section = GameMap.getSection(sectionId) ?: run {
                                commandExecutor.sendMessage("§c赛段 $sectionId 不存在")
                                return@anyExecutor
                            }
                            val radius = commandArguments["radius"] as Double
                            GameMap.turnOffRadiusParticle()
                            GameMap.setSection(sectionId, MapSection(section.getData().copy(radius = radius)))
                            commandExecutor.sendMessage("§a已将赛段 $sectionId 的半径设置为 $radius")
                            GameMap.turnOnRadiusParticle()
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
                            val location = (commandArguments["location"] as Location).toSimpleLocation()
                            GameMap.turnOffMapParticle()
                            GameMap.turnOffRadiusParticle()
                            if (firstSectionId >= 0) {
                                val firstSection = mapSection(firstSectionId, commandExecutor) ?: return@anyExecutor
                                firstSection.tunOffRadiusParticleTask()
                                GameMap.setSection(
                                    firstSectionId,
                                    MapSection(firstSection.getData().copy(endPos = location))
                                )
                            }
                            if (GameMap.haveSection(secondSectionId)) {
                                val secondSection = mapSection(secondSectionId, commandExecutor) ?: return@anyExecutor
                                secondSection.tunOffRadiusParticleTask()
                                GameMap.setSection(
                                    secondSectionId,
                                    MapSection(secondSection.getData().copy(beginPos = location))
                                )
                            }
                            GameMap.turnOnMapParticle()
                            GameMap.turnOnRadiusParticle()
                            commandExecutor.sendMessage("§a已将赛道关键点 $secondSectionId 设置为 ${location.toDebugText()}")
                        }
                    }
                }
            }
            literalArgument("split") {
                integerArgument("section") {
                    locationArgument("point") {
                        doubleArgument("radius") {
                            anyExecutor { commandExecutor, commandArguments ->
                                val point = (commandArguments["point"] as Location).toSimpleLocation()
                                val radius = commandArguments["radius"] as Double
                                val firstSectionId = commandArguments["section"] as Int
                                val firstSection = mapSection(firstSectionId, commandExecutor) ?: return@anyExecutor
                                GameMap.turnOffMapParticle()
                                GameMap.turnOffRadiusParticle()
                                GameMap.setSection(
                                    firstSectionId,
                                    MapSection(firstSection.getData().copy(endPos = point))
                                )
                                val newSection = MapSection(
                                    MapSectionData(
                                        point,
                                        firstSection.endPos,
                                        radius
                                    )
                                )
                                GameMap.insertSection(firstSectionId + 1, newSection)
                                commandExecutor.sendMessage("§a已将赛段 $firstSectionId 在关键点 ${point.toDebugText()} 处分割")
                                GameMap.turnOnMapParticle()
                                GameMap.turnOnRadiusParticle()
                            }
                        }
                    }
                }
            }
            literalArgument("create") {
                locationArgument("endPos") {
                    doubleArgument("radius") {
                        anyExecutor { commandExecutor, commandArguments ->
                            GameMap.turnOffMapParticle()
                            GameMap.turnOffRadiusParticle()
                            val endPos = (commandArguments["endPos"] as Location).toSimpleLocation()
                            val radius = commandArguments["radius"] as Double
                            val sectionId = GameMap.sectionCount()
                            val lastPoint = GameMap.getSection(sectionId - 1)?.endPos ?: SimpleLocation(0.0, 0.0, 0.0)
                            GameMap.setSection(sectionId, MapSection(MapSectionData(lastPoint, endPos, radius)))
                            GameMap.turnOnMapParticle()
                            GameMap.turnOnRadiusParticle()
                            commandExecutor.sendMessage("§a已创建赛段 $sectionId")
                        }
                    }
                }
            }
        }
    }

    private fun mapSection(
        firstSectionId: Int,
        commandExecutor: CommandSender,
    ): MapSection? = GameMap.getSection(firstSectionId) ?: run {
        commandExecutor.sendMessage("§c赛段 $firstSectionId 不存在")
        GameMap.turnOffMapParticle()
        GameMap.turnOffRadiusParticle()
        return null
    }
}