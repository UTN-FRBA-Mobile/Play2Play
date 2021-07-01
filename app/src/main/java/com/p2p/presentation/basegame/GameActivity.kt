package com.p2p.presentation.basegame

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.framework.bluetooth.BluetoothConnectionThread.Companion.PEER_ID
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_READ
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_WRITE_ERROR
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.MESSAGE_WRITE_SUCCESS
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.ON_CLIENT_CONNECTION_FAILURE
import com.p2p.framework.bluetooth.BluetoothHandlerMessages.ON_CLIENT_CONNECTION_SUCCESS
import com.p2p.model.VisibleLoadingScreen
import com.p2p.model.base.message.Conversation
import com.p2p.model.base.message.Message
import com.p2p.presentation.base.BaseMVVMActivity
import com.p2p.utils.Logger
import com.p2p.utils.hideKeyboard
import kotlin.reflect.KClass

abstract class GameActivity<E : SpecificGameEvent, VM : GameViewModel> :
    BaseMVVMActivity<GameEvent, VM>(R.layout.activity_base) {

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
                val conversationMessage = it.toConversation()
                Logger.d(TAG, "Read: ${conversationMessage.lastMessage}")
                viewModel.receiveMessage(conversationMessage)
                true
            }
            MESSAGE_WRITE_SUCCESS -> {
                val conversationMessage = it.toConversation()
                Logger.d(TAG, "Sent successfully: ${conversationMessage.lastMessage}")
                viewModel.onSentSuccessfully(conversationMessage)
                true
            }
            MESSAGE_WRITE_ERROR -> {
                val message = it.toMessage()
                Logger.d(TAG, "Cannot write: $message")
                viewModel.onSentError(message)
                true
            }
            ON_CLIENT_CONNECTION_SUCCESS -> {
                viewModel.onClientConnectionSuccess()
                true
            }
            ON_CLIENT_CONNECTION_FAILURE -> {
                viewModel.onClientConnectionFailure()
                true
            }
            else -> false
        }
    }
    private val gameConnectionType: String by lazy {
        requireNotNull(intent.getStringExtra(GameConnectionType.EXTRA)) {
            "The connection type wasn't sent. Use the start method of [GameActivity]."
        }
    }
    private val device: BluetoothDevice? by lazy { intent.getParcelableExtra(SERVER_DEVICE_EXTRA) }
    private val objectMapper by lazy { jacksonObjectMapper() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadingScreen.observe(this) { loading ->
            if (loading.isLoading) hideKeyboard()
            findViewById<View>(R.id.activity_progress_overlay).isVisible = loading.isLoading
            when (loading) {
                is VisibleLoadingScreen ->
                    findViewById<TextView>(R.id.progress_text).text = loading.waitingText
                else -> {
                }
            }
        }
        viewModel.error.observe(this) { error ->
            val errorView = findViewById<View>(R.id.error_view)
            errorView.isVisible = error != null
            error?.run {
                findViewById<ImageView>(R.id.error_image).setImageResource(image)
                findViewById<TextView>(R.id.error_text).setText(text)
                findViewById<TextView>(R.id.error_button).setOnClickListener {
                    errorView.isVisible = false
                    onRetry()
                }
            }
        }
    }

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
        MaterialAlertDialogBuilder(this)
            .setMessage(instructions)
            //It is positive to be shown on the right
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                // Respond to positive button press
            }
            .show()
    }

    private fun android.os.Message.toConversation(): Conversation {
        return Conversation(this.toMessage(), data.getLong(PEER_ID))
    }

    private fun android.os.Message.toMessage(): Message {
        return objectMapper.readValue(obj as ByteArray, Message::class.java)
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
