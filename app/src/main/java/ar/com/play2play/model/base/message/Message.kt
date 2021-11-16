package ar.com.play2play.model.base.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import ar.com.play2play.model.impostor.message.ImpostorAssignWord
import ar.com.play2play.model.impostor.message.ImpostorEndGame
import ar.com.play2play.model.truco.message.*
import ar.com.play2play.model.tuttifrutti.message.*
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
    JsonSubTypes.Type(value = WrongGameJoinedMessage::class),
    JsonSubTypes.Type(value = RoomIsAlreadyFullMessage::class),
    JsonSubTypes.Type(value = CannotJoinToStartedGameMessage::class),
    JsonSubTypes.Type(value = RejoinNameErrorMessage::class),
    JsonSubTypes.Type(value = GoodbyePlayerMessage::class),
    JsonSubTypes.Type(value = PauseGameMessage::class),
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
    JsonSubTypes.Type(value = TrucoWelcomeBack::class),
    JsonSubTypes.Type(value = TrucoActionMessage::class),
    JsonSubTypes.Type(value = TrucoPlayCardMessage::class),
)
abstract class Message(private val type: String) : Serializable
