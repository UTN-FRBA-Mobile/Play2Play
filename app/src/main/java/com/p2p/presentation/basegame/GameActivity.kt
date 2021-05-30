package com.p2p.presentation.basegame

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.framework.bluetooth.BluetoothConnectionThread.Companion.MESSAGE_READ
import com.p2p.framework.bluetooth.BluetoothConnectionThread.Companion.MESSAGE_WRITE_ERROR
import com.p2p.framework.bluetooth.BluetoothConnectionThread.Companion.MESSAGE_WRITE_SUCCESS
import com.p2p.framework.bluetooth.BluetoothConnectionThread.Companion.SENDER_ID
import com.p2p.model.message.Message
import com.p2p.model.message.MessageReceived
import com.p2p.presentation.base.BaseMVVMActivity
import com.p2p.utils.Logger
import kotlin.reflect.KClass

abstract class GameActivity<E : SpecificGameEvent, VM : GameViewModel>(
    @LayoutRes layout: Int = R.layout.activity_base
) : BaseMVVMActivity<GameEvent, VM>(layout) {

    protected val gameViewModelFactory: GameViewModelFactory
        get() = GameViewModelFactory(this, gameViewModelFactoryData)

    protected val gameViewModelFactoryData: GameViewModelFactory.Data
        get() = GameViewModelFactory.Data(
            handler = handler,
            gameConnectionType = gameConnectionType,
            device = device
        )

    private val handler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            MESSAGE_READ -> {
                val message = it.toMessage()
                Logger.d(TAG, "Read: $message")
                viewModel.receiveMessage(MessageReceived(message, it.data.getLong(SENDER_ID)))
                true
            }
            MESSAGE_WRITE_SUCCESS -> {
                val message = it.toMessage()
                Logger.d(TAG, "Sent successfully: $message")
                viewModel.onSentSuccessfully(message)
                true
            }
            MESSAGE_WRITE_ERROR -> {
                val message = it.toMessage()
                Logger.d(TAG, "Cannot write: $message")
                viewModel.onSentError(message)
                true
            }
            else -> false
        }
    }
    private val gameConnectionType: String by lazy {
        requireNotNull(intent.getStringExtra(GameConnectionType.EXTRA)) {
            "The server device wasn't sent. Use the start method of [GameActivity]."
        }
    }
    private val device: BluetoothDevice? by lazy { intent.getParcelableExtra(SERVER_DEVICE_EXTRA) }
    private val objectMapper by lazy { jacksonObjectMapper() }

    final override fun onEvent(event: GameEvent) = when (event) {
        GoToCreate -> goToCreate()
        GoToClientLobby -> goToClientLobby()
        GoToServerLobby -> goToServerLobby()
        GoToPlay -> goToPlay()
        is OpenInstructions -> showInstructions(event.instructions)
        is SpecificGameEvent -> {
            try {
                @Suppress("UNCHECKED_CAST") // Read the catch-throw message to understand this :)
                onGameEvent(event as E)
            } catch (e: ClassCastException) {
                throw IllegalStateException(
                    "On a specific implementation of a game view model must be " +
                            "dispatched specific game events only of the current game.", e
                )
            }
        }
    }

    protected abstract fun goToCreate()

    protected abstract fun goToPlay()

    protected abstract fun goToClientLobby()

    protected abstract fun goToServerLobby()

    protected open fun onGameEvent(event: E) {}

    private fun showInstructions(instructions: String) {
        MaterialAlertDialogBuilder(baseContext)
            .setMessage(instructions)
            //It is positive to be shown on the right
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                // Respond to positive button press
            }
            .show()
    }

    private fun android.os.Message.toMessage(): Message {
        val byteArray = (obj as ByteArray).copyOfRange(0, arg1)
        return objectMapper.readValue(byteArray, Message::class.java)
    }

    companion object {

        private const val TAG = "P2P_GAME_ACTIVITY"
        private const val SERVER_DEVICE_EXTRA = "SERVER_DEVICE_EXTRA"

        fun startCreate(clazz: KClass<*>, context: Context, customizeIntent: Intent.() -> Unit = {}) {
            context.startActivity(Intent(context, clazz.java).apply {
                putExtra(GameConnectionType.EXTRA, GameConnectionType.SERVER)
                customizeIntent()
            })
        }

        fun startJoin(
            clazz: KClass<*>,
            context: Context,
            serverDevice: BluetoothDevice,
            customizeIntent: Intent.() -> Unit = {}
        ) {
            context.startActivity(Intent(context, clazz.java).apply {
                putExtra(GameConnectionType.EXTRA, GameConnectionType.CLIENT)
                putExtra(SERVER_DEVICE_EXTRA, serverDevice)
                customizeIntent()
            })
        }
    }
}
