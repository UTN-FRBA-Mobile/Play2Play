package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.p2p.model.tuttifrutti.message.TuttiFruttiEnoughForMeEnoughForAllMessage
import com.p2p.model.tuttifrutti.message.TuttiFruttiSendWordsMessage
import com.p2p.model.tuttifrutti.TuttiFruttiStartGame
import java.io.Serializable

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ClientHandshakeMessage::class),
    JsonSubTypes.Type(value = ServerHandshakeMessage::class),
    JsonSubTypes.Type(value = TuttiFruttiStartGame::class),
    JsonSubTypes.Type(value = TuttiFruttiEnoughForMeEnoughForAllMessage::class),
    JsonSubTypes.Type(value = TuttiFruttiSendWordsMessage::class),
)
abstract class Message(private val type: String) : Serializable
