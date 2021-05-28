package com.p2p.model.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ClientHandshakeMessage::class, name = ClientHandshakeMessage.TYPE),
    JsonSubTypes.Type(value = ServerHandshakeMessage::class, name = ServerHandshakeMessage.TYPE),
)
abstract class Message(private val type: String) : Serializable
