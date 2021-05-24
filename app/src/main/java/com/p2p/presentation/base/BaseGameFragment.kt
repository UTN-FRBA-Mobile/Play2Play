package com.p2p.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.databinding.BaseGameBinding
import com.p2p.presentation.home.games.Game

/**
 * Base implementation of a [BaseGameFragment] used to simplify boilerplate.
 * It is a [BaseFragment] with base content of game screen
 * [GVB]: GameViewBinding
 * [E]: Event
 * [VM]: BaseViewModel with the same event defined
 */
abstract class BaseGameFragment<GVB : ViewBinding, E : Any, VM : BaseViewModel<E>>(val instructions: String) :
    BaseFragment<BaseGameBinding, E, VM>() {

    /** Common properties of games to be shown in the layout */
    abstract val gameData: Game

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
        binding.header.title = context?.getText(gameData.nameRes)
        binding.header.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.instructions -> {
                    showInstructions()
                    true
                }
                else -> false
            }
        }
        initGameUI()
    }

    private fun showInstructions() =
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(instructions)
            //It is positive to be shown on the right
            .setPositiveButton(resources.getString(R.string.close_button)) { _, _ ->
                // Respond to positive button press
            }
            .show()

}
