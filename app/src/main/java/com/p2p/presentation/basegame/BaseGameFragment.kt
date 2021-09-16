package com.p2p.presentation.basegame

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.p2p.R
import com.p2p.databinding.BaseGameBinding
import com.p2p.presentation.base.BaseFragment
import com.p2p.presentation.base.BaseViewModel

/**
 * Base implementation of a [BaseGameFragment] used to simplify boilerplate.
 * It is a [BaseFragment] with base content of game screen
 * [GVB]: GameViewBinding
 * [E]: Event
 * [VM]: BaseViewModel with the same event defined
 */
abstract class BaseGameFragment<GVB : ViewBinding, E : Any, VM : BaseViewModel<E>, GVM : GameViewModel>(protected val isAddedToBackStack: Boolean = false) :
    BaseFragment<BaseGameBinding, E, VM>() {

    protected abstract val gameViewModel: GVM

    protected open val isHeaderVisible = true

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> BaseGameBinding =
        { inflater, container, boolean ->
            val baseView = BaseGameBinding.inflate(inflater)
            _gameBinding = gameInflater(inflater, container, boolean)
            val gameView = gameBinding.root
            baseView.content.addView(gameView)
            if( !isAddedToBackStack ) {
                baseView.header.navigationIcon = null
            }
            baseView
        }

    /** The binding is used to access to the views declared on the gameLayout [GVB]. */
    private var _gameBinding: GVB? = null
    protected val gameBinding
        get() = requireNotNull(_gameBinding) {
            "The game view binding is required but the view was already destroyed."
        }

    abstract val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> GVB

    @CallSuper
    override fun initUI() {
        binding.header.isVisible = isHeaderVisible
        setHeaderEvents(binding.header)
    }

    @CallSuper
    override fun onCreate(
        savedInstanceState: Bundle?
    ){
        super.onCreate(savedInstanceState)
        if(!isAddedToBackStack) {
            activity?.onBackPressedDispatcher?.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        AlertDialog.Builder(requireContext())
                            .setMessage(R.string.exit_game)
                            .setCancelable(false)
                            .setPositiveButton(R.string.exit_game_yes) { _, _ ->
                                activity?.finish()
                            }
                            .setNegativeButton(R.string.exit_game_no, null)
                            .show()
                    }
                })
        }
    }

    @CallSuper
    override fun setupObservers() {
        super.setupObservers()
        observe(gameViewModel.game) { binding.header.title = context?.getText(it.nameRes) }
    }

    private fun setHeaderEvents(header: MaterialToolbar) {
        header.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.instructions -> {
                    gameViewModel.showInstructions()
                    true
                }
                else -> false
            }
        }
        header.setNavigationOnClickListener { activity?.onBackPressed() }
    }
}
