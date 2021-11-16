package ar.com.play2play.presentation.tuttifrutti.create.categories

/** A base class for all events that could occur on the categories events screen. */
sealed class TuttiFruttiCategoriesEvents

class GoToSelectRounds(val categories: List<Category>) : TuttiFruttiCategoriesEvents()