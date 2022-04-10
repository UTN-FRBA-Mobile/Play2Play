package ar.com.play2play.presentation.home.games

/** A base class for all events that could occur on the Games screen. */
sealed class GamesEvents

object GoToCreateTuttiFrutti : GamesEvents()

object GoToCreateImpostor : GamesEvents()

object GoToCreateTruco : GamesEvents()

object TurnOnBluetooth : GamesEvents()

class JoinGame(val game: Game) : GamesEvents()
