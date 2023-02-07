package com.cludivers.kz2wdsheepwars.game

import java.util.UUID

val playersInfo = emptyMap<UUID, PlayerInfo>().toMutableMap()

class PlayerInfo(val playerId: UUID) {
    var game: Game? = null
}