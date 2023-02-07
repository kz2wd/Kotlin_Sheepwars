package com.cludivers.kz2wdsheepwars

import com.cludivers.kz2wdsheepwars.game.GameListener
import com.cludivers.kz2wdsheepwars.game.LaunchGameCommand
import com.cludivers.kz2wdsheepwars.sheep_logic.SheepLauncher
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNUSED")
class Kz2wdSheepwars : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        logger.info("Hello World!")


        // Register Listener and tasks
        val sheepLauncher = SheepLauncher()
        server.pluginManager.registerEvents(sheepLauncher, this)
        sheepLauncher.launchSheepControl(this, 5)
        server.pluginManager.registerEvents(GameListener(), this)

        // Activate commands
        this.getCommand("start")?.setExecutor(LaunchGameCommand(this))

        val world = Bukkit.getWorld("world")
        world?.setGameRule(GameRule.DO_ENTITY_DROPS, false)

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}