package com.cludivers.kz2wdsheepwars.game

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.lang.Float.min

class Game(
    private val teams:  List<MutableList<Player>>,
    val gameMap: GameMap,
    private val plugin: JavaPlugin,
    private val sheepEveryXSeconds: Int = 10,
    private val tickUpdateFrequency: Int = 5
)
{
    // TODO : Link this to the sheep lancer IDK how
    private val wools = listOf(
        Material.CYAN_WOOL,
        Material.RED_WOOL, Material.PINK_WOOL, Material.LIGHT_BLUE_WOOL,
        Material.YELLOW_WOOL, Material.GREEN_WOOL,
        Material.LIME_WOOL, Material.ORANGE_WOOL, Material.GRAY_WOOL)

    val alivePlayers = teams.toList()
    val players = teams.mapIndexed{ index, team ->
        team.associate { player -> player.uniqueId to PlayerGameInfo(player, index, player.location) }
    }.flatMap { it.entries }.associate { it.key to it.value }

    var runner: BukkitRunnable? = null

    private fun unbreakableItem(material: Material): ItemStack {
        val itemstack = ItemStack(material)
        val itemMeta = itemstack.itemMeta
        itemMeta.isUnbreakable = true
        itemstack.itemMeta = itemMeta
        return itemstack
    }

    private fun armorPiece(armorMaterial: Material): ItemStack{
        val itemStack = unbreakableItem(armorMaterial)
        itemStack.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2)
        itemStack.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2)
        return itemStack
    }

    private fun givePlayerStuff(player: Player){
        player.inventory.forEach { it?.amount = 0 } // Empty inventory prior to game
        player.inventory.helmet = armorPiece(Material.LEATHER_HELMET)
        player.inventory.chestplate = armorPiece(Material.LEATHER_CHESTPLATE)
        player.inventory.leggings = armorPiece(Material.LEATHER_LEGGINGS)
        player.inventory.boots = armorPiece(Material.LEATHER_BOOTS)
        player.inventory.addItem(unbreakableItem(Material.WOODEN_SWORD))
        val bow = unbreakableItem(Material.BOW)
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1)
        player.inventory.addItem(bow)
        player.inventory.setItem(26, ItemStack(Material.ARROW))

    }

    fun start(){
        players.values.forEach {
            val playerInfo = PlayerInfo(it.player.uniqueId)
            playerInfo.game = this
            playersInfo[it.player.uniqueId] = playerInfo
            givePlayerStuff(it.player)
            it.player.gameMode = GameMode.SURVIVAL
        }

        chatPlayers(Component.text("La partie commence !"))
        // Spawn players
        val spawnPositions = gameMap.teamsSpawnPositions.map { it.shuffled() }
        (teams zip spawnPositions).forEach { itt ->
            itt.first.withIndex().forEach {
                it.value.teleport(itt.second[it.index % itt.second.size])
            }
        }

        // Launch game runner
        runner = object: BukkitRunnable() {
            private val sheepWaitingTickAmount = sheepEveryXSeconds * 20
            private var count: Int = sheepWaitingTickAmount

            override fun run() {
                if (count >= sheepWaitingTickAmount){
                    alivePlayers.flatten().forEach {
                        it.inventory.addItem(ItemStack(wools.random()))
                    }
                    count = 0
                }

                alivePlayers.flatten().forEach {
                    if (it.isSneaking && it.exp < 1){
                        it.exp = min(it.exp + .1F, 1F)
                    }
                }
                count += tickUpdateFrequency
            }
        }

        runner?.runTaskTimer(plugin, 60, tickUpdateFrequency.toLong())

    }

    fun died(player: Player){
        player.gameMode = GameMode.SPECTATOR
        player.spigot().respawn()
        player.teleport(gameMap.respawnLocation)
        val playerTeam = alivePlayers[players[player.uniqueId]?.team!!]
        playerTeam.remove(player)
        if (playerTeam.isEmpty()){
            endGame()
        }
        player.inventory.forEach {
            if ( wools.contains(it?.type) ) {
                player.killer?.inventory?.addItem(it)
            }
        }


    }

    fun playerLeftGame(player: Player){
        playersInfo[player.uniqueId]?.game = null
    }

    fun chatPlayers(msg: Component){
        players.values.forEach {
            it.player.sendMessage(msg)
        }
    }

    private fun winningTeam(): List<Player>{
        if (teams.size == 1) return teams[0]  // I am alone :(
        return teams[if (teams[0].isEmpty()) 1 else 0]
    }

    fun endGame(){

        val winnersMessage = Component.text(winningTeam().joinToString(" ") { it.name } + " ont gagné la partie !")
        chatPlayers(winnersMessage)

        object: BukkitRunnable(){
            var countdown = 5
            override fun run() {
                chatPlayers(Component.text("Le monde se fermera dans $countdown seconde${if (countdown > 1) "s" else ""}"))
                countdown -= 1
                if (countdown <= 0){
                    runner?.cancel()
                    players.values.forEach { playerLeftGame(it.player) }
                    gameMap.close()
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 100, 20)
    }

    private fun getPlayerTeam(player: Player): MutableList<Player> {
        return if (teams[0].contains(player)){
            teams[0]
        } else {
            teams[1]
        }
    }

    fun areTeamMates(playerOne: Player, playerTwo: Player): Boolean {
        return getPlayerTeam(playerOne).contains(playerTwo)
    }
}