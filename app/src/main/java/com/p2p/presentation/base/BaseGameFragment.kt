package com.p2p.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.p2p.data.BaseGameData
import com.p2p.databinding.BaseGameBinding

/**
 * Base implementation of a [BaseGameFragment] used to simplify boilerplate.
 * It is a [BaseFragment] with base content of game screen
 * [GVB]: GameViewBinding
 * [E]: Event
 * [VM]: BaseViewModel with the same event defined
 */
abstract class BaseGameFragment<GVB : ViewBinding, E : Any, VM : BaseViewModel<E>>
    : BaseFragment<BaseGameBinding, E, VM>() {

    /** Common properties of games to be shown in the layout */
    abstract val baseGameData: BaseGameData

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> BaseGameBinding =
        { inflater, container, boolean ->
            val baseView = BaseGameBinding.inflate(inflater)
            _gameBinding = gameInflater(inflater, container, boolean)
            val gameView = gameBinding.root
            baseView.content.addView(gameView)
            baseView
        }

    /** The binding is used to access to the views declared on the gameLayout [GVB]. */
    private var _gameBinding: GVB? = null
    protected val gameBinding
        get() = requireNotNull(_gameBinding) {
            "The game view binding is required but the view was already destroyed."
        }

    abstract val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> GVB

    /** Invoked when the view is initialized and should initialize the game view that requires it. */
    protected open fun initGameUI() {}

    override fun initUI() {
        binding.topAppBar.title = context?.getText(baseGameData.name)
        //TODO put dialog
        //binding.topAppBar.title = context?.getText(baseData.name)
        initGameUI()
    }

}
