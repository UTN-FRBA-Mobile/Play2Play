package com.p2p.data.bluetooth

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.p2p.framework.bluetooth.basemessage.HandshakeMessage
import java.io.Serializable

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = HandshakeMessage::class, name = HandshakeMessage.TYPE)
)
abstract class Message(private val type: String) : Serializable
