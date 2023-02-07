package com.cludivers.kz2wdsheepwars.geo

import org.bukkit.Location

object WorldGeo {
    fun circle(loc: Location, r: Int, h: Int, hollow: Boolean, sphere: Boolean, plus_y: Int): List<Location> {
        val circleblocks: MutableList<Location> = ArrayList()
        val cx = loc.blockX
        val cy = loc.blockY
        val cz = loc.blockZ
        for (x in cx - r..cx + r) for (z in cz - r..cz + r) for (y in (if (sphere) cy - r else cy) until if (sphere) cy + r else cy + h) {
            val dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + if (sphere) (cy - y) * (cy - y) else 0).toDouble()
            if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                val l = Location(loc.world, x.toDouble(), (y + plus_y).toDouble(), z.toDouble())
                circleblocks.add(l)
            }
        }
        return circleblocks
    }
}