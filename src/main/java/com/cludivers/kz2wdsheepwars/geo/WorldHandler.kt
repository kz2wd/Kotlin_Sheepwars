package com.cludivers.kz2wdsheepwars.geo

import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.function.Consumer

class WorldHandler private constructor() {
    var temporaryWorld: MutableList<World?> = ArrayList()

    init {
        INSTANCE = this
    }

    fun addWorld(name: String, temporary: Boolean): World? {
        val worldFolder = File(Bukkit.getWorldContainer(), name)
        if (worldFolder.isDirectory) {
            val uid = File(worldFolder, "uid.dat")
            if (uid.exists()) {
                uid.delete()
            }
        }
        val wc = WorldCreator(name)
        Bukkit.getServer().createWorld(wc)
        if (temporary) {
            temporaryWorld.add(Bukkit.getServer().getWorld(name))
        }

        println("[WORLD HANDLER] CREATED WORLD : $name")

        // SETUP GAMERULE HERE IF YOU NEED SOME
        return Bukkit.getServer().getWorld(name)
    }

    fun removeWorld(world: World) {
        removePlayers(world)
        Bukkit.getServer().unloadWorld(world, false)
        deleteDirectory(world.worldFolder)
    }

    private fun removePlayers(world: World?) {
        world!!.players.forEach(Consumer { p: Player ->
            p.teleport(
                Bukkit.getWorld("world")!!.spawnLocation
            )
        })
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun createWorld(templateWorld: File, temporary: Boolean, prefix: String = templateWorld.name): World? {
        var prefix = prefix
        val mapFolder: File

        // Prefix must be at least of length 3, I don't know why
        if (prefix.length < 3) {
            prefix += "sup"
        }
        val tempFile = File.createTempFile(prefix, "", Bukkit.getWorldContainer())
        tempFile.delete()
        Files.createDirectories(tempFile.toPath())
        mapFolder = File(tempFile.path)
        FileUtils.copyDirectory(templateWorld, mapFolder)
        return addWorld(mapFolder.name, temporary)
    }

    companion object {
        var INSTANCE = WorldHandler()
        fun deleteDirectory(directoryToBeDeleted: File): Boolean {
            val allContents = directoryToBeDeleted.listFiles()
            if (allContents != null) {
                for (file in allContents) {
                    deleteDirectory(file)
                }
            }
            return directoryToBeDeleted.delete()
        }
    }
}