package com.p2p.framework.bluetooth

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque

class MessageSenderThread : Thread() {

    private var isClosed = false
    private var messagesQueue: BlockingQueue<ConnectionMessage> = LinkedBlockingDeque()

    init {
        start()
    }

    fun close() {
        isClosed = true
    }

    fun putMessage(
        connection: BluetoothConnectionThread,
        byteArray: ByteArray,
        length: Int,
        isConversation: Boolean
    ) = messagesQueue.put(ConnectionMessage(connection, byteArray, length, isConversation))

    override fun run() {
        while (!isClosed) {
            // Wait until a new message arrived and write it
            messagesQueue.take().write()

            /**
             * Wait [DELAY_BETWEEN_MESSAGES] seconds between sending messages.
             *
             * We must do this because if the messages are sent very closely, one after the other,
             * then it can cause the receiver to read both at the same time.
             * If that happens then the second message won't be read.
             */
            sleep(DELAY_BETWEEN_MESSAGES)
        }
    }

    private class ConnectionMessage(
        private val connection: BluetoothConnectionThread,
        private val bytes: ByteArray,
        private val length: Int,
        private val isConversation: Boolean
    ) {

        fun write() = connection.write(bytes, length, isConversation)
    }

    companion object {

        private const val DELAY_BETWEEN_MESSAGES: Long = 2_000
    }
}