package com.cludivers.kz2wdsheepwars.sheep_logic

import com.cludivers.kz2wdsheepwars.geo.WorldGeo
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Sheep
import org.bukkit.potion.PotionEffect

abstract class SpellAction {

    var done: Boolean = false

    abstract fun execute()

}

abstract class SpellArea (val sheep: Sheep) : SpellAction() {
}

class ExplosionArea(sheep: Sheep, val explosionRadius: Float): SpellArea(sheep) {
    override fun execute() {
        sheep.location.createExplosion(explosionRadius)
        done = true
    }
}

open class EffectArea(sheep: Sheep, val potionEffect: PotionEffect, val radius: Double,val particle: Particle) : SpellArea(sheep) {
    override fun execute() {
        sheep.location.getNearbyPlayers(radius).forEach {
            it.addPotionEffect(potionEffect)
        }
        sheep.location.world.spawnParticle(particle, sheep.location, 8, radius, radius, radius)
        done = true
    }
}

open class EffectsArea(sheep: Sheep, val potionEffects: List<PotionEffect>, val radius: Double,val particle: Particle) : SpellArea(sheep) {
    override fun execute() {
        sheep.location.getNearbyPlayers(radius).forEach {
            for (potionEffect in potionEffects) {
                it.addPotionEffect(potionEffect)
            }
        }
        sheep.location.world.spawnParticle(particle, sheep.location, 8, radius, radius, radius)
        done = true
    }
}

class IceArea(sheep: Sheep, val radius: Float): SpellArea(sheep) {
    override fun execute() {
        WorldGeo.circle(sheep.location, radius.toInt(), radius.toInt(), hollow = false, sphere = true, plus_y = 0).forEach {
            if (!it.block.type.isAir){
                it.block.type = Material.PACKED_ICE
            }

        }
        done = true
    }
}

class FireExplosionArea(sheep: Sheep, val explosionRadius: Float): SpellArea(sheep) {
    override fun execute() {
        sheep.location.createExplosion(explosionRadius)
        WorldGeo.circle(sheep.location, explosionRadius.toInt(), explosionRadius.toInt(), hollow = false, sphere = true, plus_y = 0).forEach {
            if (it.block.isEmpty) {
                it.block.type = Material.FIRE
            }
        }
        done = true
    }
}

