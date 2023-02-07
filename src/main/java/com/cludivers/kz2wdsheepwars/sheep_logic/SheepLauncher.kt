package com.cludivers.kz2wdsheepwars.sheep_logic

import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Sheep

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.ConcurrentHashMap


class SheepLauncher : Listener {

    private val awayVelocity = 2.5f
    private val hereVelocity = 0f

    private val wools = listOf(Material.BLACK_WOOL, Material.BLUE_WOOL, Material.BROWN_WOOL, Material.CYAN_WOOL,
        Material.GRAY_WOOL, Material.RED_WOOL, Material.PINK_WOOL, Material.PURPLE_WOOL, Material.LIGHT_BLUE_WOOL,
        Material.YELLOW_WOOL, Material.GREEN_WOOL, Material.WHITE_WOOL, Material.LIGHT_GRAY_WOOL,
        Material.LIME_WOOL, Material.MAGENTA_WOOL, Material.ORANGE_WOOL)

    private val sheepSpells = ConcurrentHashMap<SheepSpell, Any>()

    @EventHandler
    fun onWoolClicked(event : PlayerInteractEvent) {

        if (! event.action.isRightClick) return
        if (! wools.contains(event.material)) return
        event.isCancelled = true

        val powerLevel = event.player.exp + 1

        when (event.material) {
            Material.RED_WOOL -> {
                val sheep = summonSheep(event, DyeColor.RED, awayVelocity)
                sheepSpells[SheepSpell(ExplosionArea(sheep, 3f * powerLevel), (60 * powerLevel).toInt(), 0, 1, sheep)] = 0
            }
            Material.PINK_WOOL -> {
                val sheep = summonSheep(event, DyeColor.PINK, hereVelocity)
                sheepSpells[SheepSpell(
                    EffectArea(sheep, PotionEffect(
                    PotionEffectType.HEAL, 1, (1 * powerLevel).toInt()
                    ), 4.0, Particle.HEART),
                    0, 10, 10, sheep)] = 0
            }
            Material.YELLOW_WOOL -> {
                val sheep = summonSheep(event, DyeColor.YELLOW, hereVelocity)
                sheepSpells[SheepSpell(
                    EffectArea(sheep, PotionEffect(
                    PotionEffectType.ABSORPTION, 100000, (1 * powerLevel).toInt()
                    ), 4.0, Particle.WAX_ON),
                    30, 30, 3, sheep)] = 0
            }
            Material.GREEN_WOOL -> {
                val sheep = summonSheep(event, DyeColor.GREEN, awayVelocity)
                sheepSpells[SheepSpell(
                    EffectArea(sheep, PotionEffect(
                    PotionEffectType.POISON, (40 * powerLevel).toInt(), (1 * powerLevel).toInt()
                    ), 6.0, Particle.SLIME),
                    30, 30, 3, sheep)] = 0
            }
            Material.LIME_WOOL -> {
                val sheep = summonSheep(event, DyeColor.LIME, hereVelocity)
                sheepSpells[SheepSpell(
                    EffectsArea(sheep,
                    listOf(PotionEffect(PotionEffectType.SPEED, (100 * powerLevel).toInt(), (2 * powerLevel).toInt()),
                        PotionEffect(PotionEffectType.JUMP, (100 * powerLevel).toInt(), (2 * powerLevel).toInt()))
                    , 6.0, Particle.VILLAGER_HAPPY),
                    30, 30, 3, sheep)] = 0
            }
            Material.CYAN_WOOL -> {
                val sheep = summonSheep(event, DyeColor.CYAN, awayVelocity)
                sheepSpells[SheepSpell(
                    EffectsArea(sheep,
                    listOf(PotionEffect(PotionEffectType.SPEED, (100 * powerLevel).toInt(), (20 * powerLevel).toInt()),
                        PotionEffect(PotionEffectType.JUMP, (100 * powerLevel).toInt(), (20 * powerLevel).toInt()))
                    , 6.0, Particle.VILLAGER_HAPPY),
                    (30 * powerLevel).toInt(), 30, 3, sheep)] = 0
            }
            Material.LIGHT_BLUE_WOOL -> {
                val sheep = summonSheep(event, DyeColor.LIGHT_BLUE, awayVelocity)
                sheepSpells[SheepSpell(IceArea(sheep, 5f * powerLevel), (60 * powerLevel).toInt(), 1, 1, sheep)] = 0
            }
            Material.ORANGE_WOOL -> {
                val sheep = summonSheep(event, DyeColor.ORANGE, awayVelocity)
                sheepSpells[SheepSpell(FireExplosionArea(sheep, 6f * powerLevel),
                    (100 * powerLevel).toInt(), 1, 1, sheep)] = 0
            }
            Material.GRAY_WOOL -> {
                val sheep = summonSheep(event, DyeColor.GRAY, awayVelocity)
                sheepSpells[SheepSpell(ExplosionArea(sheep, 8f * powerLevel), (150 * powerLevel).toInt(), 1, 1, sheep)] = 0
            }


            else -> {}
        }

        event.item?.subtract(1)

    }


    @EventHandler
    fun onSheepDamage(event: EntityDamageEvent) {
        if ( event.entity !is Sheep ) return
        if (event.cause != DamageCause.ENTITY_ATTACK && event.cause != DamageCause.PROJECTILE ){
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onSheepDie(event: EntityDeathEvent){
        if ( event.entity !is Sheep ) return

        val sheep : Sheep = event.entity as Sheep
        val loot = ItemStack(dyeToWool(sheep.color!!))

        if ( sheep.killer is Player ){
            sheep.killer!!.inventory.addItem(loot)
        } else {
            sheep.location.world.dropItem(sheep.location, loot)
        }

        sheep.location.world.playSound(sheep.location, Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, 1.0f, 0f)
    }

    private fun dyeToWool(color: DyeColor): Material {
        when (color){
            DyeColor.WHITE -> return Material.WHITE_WOOL
            DyeColor.ORANGE -> return Material.ORANGE_WOOL
            DyeColor.MAGENTA -> return Material.MAGENTA_WOOL
            DyeColor.LIGHT_BLUE -> return Material.LIGHT_BLUE_WOOL
            DyeColor.YELLOW -> return Material.YELLOW_WOOL
            DyeColor.LIME -> return Material.LIME_WOOL
            DyeColor.PINK -> return Material.PINK_WOOL
            DyeColor.GRAY -> return Material.GRAY_WOOL
            DyeColor.LIGHT_GRAY -> return Material.LIGHT_GRAY_WOOL
            DyeColor.CYAN -> return Material.CYAN_WOOL
            DyeColor.PURPLE -> return Material.PURPLE_WOOL
            DyeColor.BLUE -> return Material.BLUE_WOOL
            DyeColor.BROWN -> return Material.BROWN_WOOL
            DyeColor.GREEN -> return Material.GREEN_WOOL
            DyeColor.RED -> return Material.RED_WOOL
            DyeColor.BLACK -> return Material.BLACK_WOOL
        }
    }

    private fun summonSheep(event: PlayerInteractEvent, color: DyeColor, velocity: Float): Sheep {
        val world = event.player.eyeLocation.world
        val sheep = world.spawnEntity(event.player.eyeLocation.add(event.player.eyeLocation.direction), EntityType.SHEEP) as Sheep
        sheep.velocity = event.player.eyeLocation.direction.multiply(velocity)
        sheep.color = color
       return sheep
    }

    fun launchSheepControl(plugin: JavaPlugin, updateEveryXTick: Int){
        val task: () -> Unit = {
            sheepSpells.forEachKey(100000) {
                it.waitingTick -= updateEveryXTick
                if (it.sheep.health <= 0){
                    sheepSpells.remove(it)
                }
                if (it.waitingTick <= 0){
                    it.action.execute()
                    it.amount -= 1
                    it.waitingTick += it.frequency
                    if (it.action.done && it.amount <= 0){
                        it.sheep.remove()
                        sheepSpells.remove(it)
                    }
                }
                it.sheep.customName(Component.text(it.waitingTick))
            }
        

        }
        Bukkit.getScheduler().runTaskTimer(plugin, task, 0, updateEveryXTick.toLong())
    }
}