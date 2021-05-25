package com.p2p.framework.bluetooth.basemessage

import com.p2p.data.bluetooth.Message

data class HelloMessage(private val name: String) : Message(TYPE) {

    companion object {

        const val TYPE = "hello"
    }
}
