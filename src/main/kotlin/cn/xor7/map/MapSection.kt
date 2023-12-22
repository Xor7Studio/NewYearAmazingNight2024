package cn.xor7.map

import org.bukkit.Location
import kotlin.math.pow
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
    val sectionLength = sqrt(
        (beginPos.x - endPos.x).pow(2.0) + (beginPos.y - endPos.y).pow(2.0) + (beginPos.z - endPos.z).pow(2.0)
    )

    private val doubleSectionLength = 2 * sectionLength
    private val halfSectionLength = sectionLength / 2
    private val `Xb^2 - Xe^2` = beginPos.x.pow(2.0) - endPos.x.pow(2.0)
    private val `Yb^2 - Ye^2` = beginPos.y.pow(2.0) - endPos.y.pow(2.0)
    private val `Zb^2 - Ze^2` = beginPos.z.pow(2.0) - endPos.z.pow(2.0)
    private val `2(Xe - Xb)` = 2 * (endPos.x - beginPos.x)
    private val `2(Ye - Yb)` = 2 * (endPos.y - beginPos.y)
    private val `2(Ze - Zb)` = 2 * (endPos.z - beginPos.z)

    fun getPosition(location: Location): Double {
        return (`Xb^2 - Xe^2` +
                `Yb^2 - Ye^2` +
                `Zb^2 - Ze^2` +
                `2(Xe - Xb)` * location.x +
                `2(Ye - Yb)` * location.y +
                `2(Ze - Zb)` * location.z
                ) / doubleSectionLength - halfSectionLength
    }
}

fun Location.positionIn(section: MapSection): Double {
    return section.getPosition(this)
}