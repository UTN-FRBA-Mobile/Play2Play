package ar.com.play2play.presentation.tuttifrutti

import ar.com.play2play.data.bluetooth.BluetoothConnectionCreator
import ar.com.play2play.data.instructions.InstructionsRepository
import ar.com.play2play.data.loadingMessages.LoadingTextRepository
import ar.com.play2play.model.LoadingMessageType
import ar.com.play2play.data.userInfo.UserSession
import ar.com.play2play.model.base.message.Conversation
import ar.com.play2play.model.tuttifrutti.message.*
import ar.com.play2play.presentation.basegame.ConnectionType
import ar.com.play2play.presentation.tuttifrutti.create.categories.Category

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

    private var enoughForAllConversation: Conversation? = null

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
            is TuttiFruttiClientReviewMessage -> {
                setFinishedRoundInfos(message.finishedRoundInfos)
                goToClientReview()
            }
            is FinalScoreMessage -> {
                setFinalScores(message.playersScores)
                goToFinalScore()
            }
        }
    }

    override fun onSentSuccessfully(conversation: Conversation) {
        when (conversation.lastMessage) {
            is TuttiFruttiEnoughForMeEnoughForAllMessage -> receiveMessage(conversation)
        }
        super.onSentSuccessfully(conversation)
    }

    override fun enoughForMeEnoughForAll() {
        startLoading(loadingTextRepository.getText(LoadingMessageType.TF_WAITING_FOR_REVIEW))
        super.enoughForMeEnoughForAll()
    }

    override fun onReceiveEnoughForAll(conversation: Conversation) {
        enoughForAllConversation = conversation
        startLoading(loadingTextRepository.getText(LoadingMessageType.TF_WAITING_FOR_REVIEW))
        super.onReceiveEnoughForAll(conversation)
    }

    private fun goToClientReview() {
        dispatchSingleTimeEvent(GoToClientReview)
    }

    override fun sendWords(categoriesWords: LinkedHashMap<Category, String>) {
        enoughForAllConversation?.let {
            connection.talk(
                it,
                TuttiFruttiSendWordsMessage(categoriesWords)
            )
        }
        enoughForAllConversation = null
    }

    override fun startGame() = goToPlay()

    override fun startRound() = goToPlay()

}
