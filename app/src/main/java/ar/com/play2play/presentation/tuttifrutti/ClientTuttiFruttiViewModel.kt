package ar.com.play2play.presentation.tuttifrutti

import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.model.base.message.Conversation
import ar.com.play2play.model.tuttifrutti.message.FinalScoreMessage
import ar.com.play2play.model.tuttifrutti.message.TuttiFruttiStartGameMessage
import ar.com.play2play.model.tuttifrutti.message.TuttiFruttiStartRoundMessage
import ar.com.play2play.presentation.basegame.ConnectionType

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
