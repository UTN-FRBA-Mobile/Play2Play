package com.p2p.model.base.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.p2p.model.impostor.message.ImpostorAssignWord
import com.p2p.model.impostor.message.ImpostorEndGame
import com.p2p.model.truco.message.*
import com.p2p.model.tuttifrutti.message.*
import java.io.Serializable

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ClientHandshakeMessage::class),
    JsonSubTypes.Type(value = ServerHandshakeMessage::class),
    JsonSubTypes.Type(value = NameInUseMessage::class),
    JsonSubTypes.Type(value = RoomIsAlreadyFullMessage::class),
    JsonSubTypes.Type(value = GoodbyePlayerMessage::class),
    JsonSubTypes.Type(value = TuttiFruttiStartGameMessage::class),
    JsonSubTypes.Type(value = TuttiFruttiEnoughForMeEnoughForAllMessage::class),
    JsonSubTypes.Type(value = TuttiFruttiSendWordsMessage::class),
    JsonSubTypes.Type(value = TuttiFruttiStartRoundMessage::class),
    JsonSubTypes.Type(value = TuttiFruttiClientReviewMessage::class),
    JsonSubTypes.Type(value = FinalScoreMessage::class),
    JsonSubTypes.Type(value = ImpostorAssignWord::class),
    JsonSubTypes.Type(value = ImpostorEndGame::class),
    JsonSubTypes.Type(value = TrucoCardsMessage::class),
    JsonSubTypes.Type(value = TrucoStartGameMessage::class),
    JsonSubTypes.Type(value = TrucoActionMessage::class),
    JsonSubTypes.Type(value = TrucoPlayCardMessage::class),
)
abstract class Message(val type: String) : Serializable
