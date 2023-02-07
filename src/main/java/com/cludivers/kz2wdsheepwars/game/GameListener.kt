package com.cludivers.kz2wdsheepwars.game

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

class GameListener: Listener {

    private fun getPlayerGame(player: Player): Game? {
        return playersInfo[player.uniqueId]?.game
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent){
        val game = getPlayerGame(event.player)
        game?.died(event.player)
        game?.playerLeftGame(event.player)

    }

    @EventHandler
    fun onPlayerDie(event: PlayerDeathEvent){
        getPlayerGame(event.player)?.died(event.player)
    }

    @EventHandler
    fun onPlayerStarve(event: FoodLevelChangeEvent){
        if (getPlayerGame(event.entity as Player) is Game){
            event.isCancelled = true
            event.entity.foodLevel = 20
        }
    }

    @EventHandler
    fun onPlayerAttackPlayer(event: EntityDamageByEntityEvent){
        val attacker = event.damager
        val attacked = event.entity
        if (attacker is Player && attacked is Player){
            val game = getPlayerGame(attacker)
            event.isCancelled = game!!.areTeamMates(attacked, attacker)
        }
    }

    private fun areLocationsSimilar(loc1: Location, loc2: Location): Boolean{
        return loc1.x.toInt() == loc2.x.toInt() && loc1.y.toInt() == loc2.y.toInt() && loc1.z.toInt() == loc2.z.toInt()
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent){
        val game = getPlayerGame(event.player)
        if (game is Game){
            if (!areLocationsSimilar(game.players[event.player.uniqueId]?.lastPlayerLocation!!, event.player.location)) {
                event.player.exp = 0F
//                Bukkit.broadcast(Component.text("Player moved from ${game.players[event.player.uniqueId]?.lastPlayerLocation} to ${event.player.location.toBlockLocation()}"))
                game.players[event.player.uniqueId]?.lastPlayerLocation = event.player.location
            }
        }
    }
}
