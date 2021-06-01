package com.p2p.presentation.tuttifrutti.create.categories

/** A base class for all events that could occur on the Games screen. */
sealed class TuttiFruttiCategoriesEvents

class GoToSelectRounds(val categories: List<Category>) : TuttiFruttiCategoriesEvents()