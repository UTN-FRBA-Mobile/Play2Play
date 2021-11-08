package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.loadingMessages.LoadingTextRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Conversation
import com.p2p.model.tuttifrutti.message.FinalScoreMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartGameMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiStartRoundMessage
import com.p2p.presentation.basegame.ConnectionType

class ClientTuttiFruttiViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository,
    loadingTextRepository: LoadingTextRepository
) : TuttiFruttiViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository,
    loadingTextRepository
) {

    override fun receiveMessage(conversation: Conversation) {
        super.receiveMessage(conversation)
        when (val message = conversation.lastMessage) {
            is TuttiFruttiStartGameMessage -> {
                lettersByRound = message.letters
                setTotalRounds(message.letters.count())
                setCategoriesToPlay(message.categories)
                startGame()
            }
            is TuttiFruttiStartRoundMessage -> startRound()
            is FinalScoreMessage -> {
                setFinalScores(message.playersScores)
                goToFinalScore()
            }
        }
    }

    override fun goToReview() = dispatchSingleTimeEvent(GoToClientReview)

    override fun startGame() = goToPlay()

    override fun startRound() = goToPlay()
}
