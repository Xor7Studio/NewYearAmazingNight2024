package cn.xor7.map

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import top.zoyn.particlelib.ParticleLib
import top.zoyn.particlelib.pobject.Line
import top.zoyn.particlelib.pobject.ParticleObject
import top.zoyn.particlelib.utils.projector.ThreeDProjector
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


/**
 *                      E
 *                     /|
 *                    / |
 *                   /  |
 *                  /   |
 *                 /    |
 *                /     |
 *               P------|
 *                \   |_|
 *                 \    |
 *                  \   |
 *                   \  |
 *                    \ |
 *                     \|
 *                      B
 * P(Xp,Yp,Zp),H(Xh, Yh, Zh),B(Xb, Yb, Zb),E(Xe, Ye, Ze)
 */
@Suppress("PrivatePropertyName", "MemberVisibilityCanBePrivate", "CanBeParameter")
class MapSection(private val data: MapSectionData) {
    val beginPos = data.beginPos
    val endPos = data.endPos
    val radius = data.radius
    val radiusSquared = radius.pow(2.0)
    val sectionLength = sqrt(
        (beginPos.x - endPos.x).pow(2.0) + (beginPos.y - endPos.y).pow(2.0) + (beginPos.z - endPos.z).pow(2.0)
    )
    val mapParticle: ParticleObject = run {
        val line = Line(beginPos.toLocation(), endPos.toLocation())
        line.period = 1L
        return@run line
    }

    private lateinit var radiusParticleTask: BukkitTask
    private var radiusParticleTaskRunning = false
    private val radiusParticlePositions: Set<Location> = run {
        val locations = mutableSetOf<Location>()
        val projector = ThreeDProjector(
            beginPos.toLocation(), Vector(
                endPos.x - beginPos.x,
                endPos.y - beginPos.y,
                endPos.z - beginPos.z
            ).normalize()
        )
        var y = 0.0
        var i = 0
        while (y < sectionLength) {
            val rad = Math.toRadians(i.toDouble())
            y += 0.01
            val x = cos(rad) * radius
            val z = sin(rad) * radius
            locations.add(projector.apply(x, y, z))
            i += 5
        }
        return@run locations
    }
    private val doubleSectionLength = 2 * sectionLength
    private val halfSectionLength = sectionLength / 2
    private val `Xb^2 - Xe^2` = beginPos.x.pow(2.0) - endPos.x.pow(2.0)
    private val `Yb^2 - Ye^2` = beginPos.y.pow(2.0) - endPos.y.pow(2.0)
    private val `Zb^2 - Ze^2` = beginPos.z.pow(2.0) - endPos.z.pow(2.0)
    private val `2(Xe - Xb)` = 2 * (endPos.x - beginPos.x)
    private val `2(Ye - Yb)` = 2 * (endPos.y - beginPos.y)
    private val `2(Ze - Zb)` = 2 * (endPos.z - beginPos.z)

    fun getPosition(location: Location): Double = ((
            `Xb^2 - Xe^2` +
                    `Yb^2 - Ye^2` +
                    `Zb^2 - Ze^2` +
                    `2(Xe - Xb)` * location.x +
                    `2(Ye - Yb)` * location.y +
                    `2(Ze - Zb)` * location.z
            ) / doubleSectionLength) + halfSectionLength

    fun getDistanceSquared(location: Location, position: Double): Double =
        (beginPos.x - location.x).pow(2.0) +
                (beginPos.y - location.y).pow(2.0) +
                (beginPos.z - location.z).pow(2.0) -
                position.pow(2.0)

    fun tunOffRadiusParticleTask() {
        if (::radiusParticleTask.isInitialized) {
            radiusParticleTaskRunning = false
            radiusParticleTask.cancel()
        }
    }

    fun getData() = data

    fun showRadiusParticle() {
        tunOffRadiusParticleTask()

        Bukkit.getScheduler().runTaskLater(ParticleLib.getInstance(), Runnable {
            radiusParticleTaskRunning = true
            radiusParticleTask = object : BukkitRunnable() {
                override fun run() {
                    if (!radiusParticleTaskRunning) {
                        return
                    }
                    radiusParticlePositions.forEach { location ->
                        Bukkit.getWorlds()[0].spawnParticle(
                            Particle.VILLAGER_HAPPY,
                            location,
                            1,
                            0.0,
                            0.0,
                            0.0,
                            0.0
                        )
                    }
                }
            }.runTaskTimer(ParticleLib.getInstance(), 0L, 2L)
        }, 2L)
    }
}