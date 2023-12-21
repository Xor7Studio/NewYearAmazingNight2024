package cn.xor7.map

import org.bukkit.Location
import kotlin.math.pow
import kotlin.math.sqrt

/*
*                     E(Xe, Ye, Ze)
*                    /|
*                   / |
*                  /  |
*                 /   |
*                /    |
*               /     |
*  P(Xp,Yp,Zp) <------| H(Xh, Yh, Zh)
*               \   |_|
*                \    |
*                 \   |
*                  \  |
*                   \ |
*                    \|
*                     B(Xb, Yb, Zb)
* */
@Suppress("PrivatePropertyName", "MemberVisibilityCanBePrivate", "CanBeParameter")
class MapSection(val beginPos: Location, val endPos: Location ){
    init {

        if (beginPos.world != endPos.world) {
            throw IllegalArgumentException("beginPos and endPos must be in the same world")
        }
    }

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

    fun getLengthToBeginPos(location: Location): Double {
        if (location.world != beginPos.world) {
            throw IllegalArgumentException("location must be in the same world as beginPos")
        }
        return (`Xb^2 - Xe^2` +
                `Yb^2 - Ye^2` +
                `Zb^2 - Ze^2` +
                `2(Xe - Xb)` * location.x +
                `2(Ye - Yb)` * location.y +
                `2(Ze - Zb)` * location.z
                ) / doubleSectionLength - halfSectionLength
    }
}