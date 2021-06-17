package com.p2p.presentation.tuttifrutti

import com.p2p.data.bluetooth.BluetoothConnectionCreator
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.data.userInfo.UserSession
import com.p2p.model.base.message.Message
import com.p2p.model.tuttifrutti.TuttiFruttiStartGame
import com.p2p.presentation.basegame.ConnectionType
import com.p2p.presentation.extensions.requireValue

class ServerTuttiFruttiViewModel(
    connectionType: ConnectionType,
    userSession: UserSession,
    bluetoothConnectionCreator: BluetoothConnectionCreator,
    instructionsRepository: InstructionsRepository
) : TuttiFruttiViewModel(
    connectionType,
    userSession,
    bluetoothConnectionCreator,
    instructionsRepository
) {
    private var gameAlreadyStarted = false

    /** Be careful: this will be called for every client on a broadcast. */
    override fun onSentSuccessfully(message: Message) {
        super.onSentSuccessfully(message)
        when (message) {
            is TuttiFruttiStartGame -> if (!gameAlreadyStarted) {
                goToPlay() // starts the game when the first StartGame message was sent successfully.
                gameAlreadyStarted = true
            }
        }
    }

    override fun startGame() {
        lettersByRound = getRandomLetters()
        connection.write(TuttiFruttiStartGame(lettersByRound, categoriesToPlay.requireValue()))
        closeDiscovery()
        goToPlay()
    }

    private fun getRandomLetters(): List<Char> =
        availableLetters.toList().shuffled().take(totalRounds.requireValue())

}
