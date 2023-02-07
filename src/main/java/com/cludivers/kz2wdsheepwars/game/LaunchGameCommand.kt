package com.cludivers.kz2wdsheepwars.game

import com.cludivers.kz2wdsheepwars.geo.Cuboid
import com.cludivers.kz2wdsheepwars.geo.WorldHandler
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.WorldCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.Integer.max
import kotlin.math.ceil

class LaunchGameCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender is Player){
            if (playersInfo[sender.uniqueId]?.game != null){
                sender.sendMessage(Component.text("Vous etes déjà dans une partie"))
                return false
            }
        }

        Bukkit.broadcast(Component.text("Démarrage d'une partie en cours"))

        val onlinePlayers = Bukkit.getOnlinePlayers().shuffled()
        val teamSize = ceil((onlinePlayers.size / 2).toDouble()).toInt()
        Bukkit.broadcast(Component.text("Team size : $teamSize"))
        val teams = onlinePlayers.chunked(teamSize).map { it.toMutableList() }.toList()

        // Map Selection
        // Only one game map for now . . .

        val mapId = if (args.isNotEmpty()) args[0].toInt() else 0
        var mapName = "cut_island"
        when (mapId) {
            1 -> mapName = "dirigeable"// replace by another name

        }

        // World Creation
        val worldTemplate = File(Bukkit.getWorldContainer(), "sheepwars_maps/$mapName")
        val world = WorldHandler.INSTANCE.createWorld(worldTemplate, true)!!

        var team1Spawns = listOf(
            Location(world, 100.0, 32.3, -7.0),
            Location(world, 112.0, 32.3, -8.0),
            Location(world, 100.0, 27.3, -7.0),
            Location(world, 112.0, 27.3, -8.0))
        var team2Spawns = listOf(
            Location(world, 100.0, 32.3, 7.0),
            Location(world, 112.0, 32.3, 8.0),
            Location(world, 100.0, 27.3, 7.0),
            Location(world, 112.0, 27.3, 8.0))

    // Maybe you need to wait some time with a runnable here to make sure the world is fully loaded
        if (mapName == "cut_island")
        {
            team1Spawns = listOf(
            Location(world, -322.0, 151.3, -179.0),
            Location(world, -317.0, 140.3, -175.0),
            Location(world, -316.0, 123.3, -174.0),
            Location(world, -356.0, 141.3, -174.0)
            )
            team2Spawns = listOf(
            Location(world, -326.0, 124.3, -196.0),
            Location(world, -353.0, 124.3, -196.0),
            Location(world, -326.0, 114.3, -198.0),
            Location(world, -326.0, 134.3, -198.0)
        )
    }
        val gameMap = GameMap(
            listOf(team1Spawns, team2Spawns),
            world,
            Cuboid(Location(world, 0.0, 0.0, 0.0), Location(world, 100.0, 100.0, 100.0)),
            Location(world, 112.0, 38.0, 0.0)
        )

        val game = Game(teams, gameMap, plugin)
        game.start()

        return true
    }
}