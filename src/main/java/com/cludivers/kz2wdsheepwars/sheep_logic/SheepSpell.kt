package com.cludivers.kz2wdsheepwars.sheep_logic

import org.bukkit.entity.Sheep

class SheepSpell(
    val action: SpellAction,
    var waitingTick: Int,
    val frequency: Int,
    var amount: Int,
    val sheep: Sheep
)