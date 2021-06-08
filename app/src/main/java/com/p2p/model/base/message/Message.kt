package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.p2p.model.tuttifrutti.StartGame
import java.io.Serializable

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ClientHandshakeMessage::class),
    JsonSubTypes.Type(value = ServerHandshakeMessage::class),
    JsonSubTypes.Type(value = StartGame::class),
)
abstract class Message(private val type: String) : Serializable
