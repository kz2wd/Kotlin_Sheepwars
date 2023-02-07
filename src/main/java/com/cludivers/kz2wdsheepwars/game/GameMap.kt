package com.cludivers.kz2wdsheepwars.game

import com.cludivers.kz2wdsheepwars.geo.Cuboid
import com.cludivers.kz2wdsheepwars.geo.WorldHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

class GameMap(
    val teamsSpawnPositions: List<List<Location>>,
    val world: World,
    val boundaries: Cuboid,
    val respawnLocation: Location
)
{


    fun close(){
        WorldHandler.INSTANCE.removeWorld(world)
    }


}