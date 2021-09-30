package com.p2p.presentation.truco

import com.p2p.model.truco.Card
import com.p2p.model.truco.PlayerTeam

data class PlayedCard(val playerTeam: PlayerTeam, val card: Card)
