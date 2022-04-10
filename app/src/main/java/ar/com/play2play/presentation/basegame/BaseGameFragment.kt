package ar.com.play2play.presentation.basegame

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import ar.com.play2play.R
import ar.com.play2play.databinding.BaseGameBinding
import ar.com.play2play.presentation.base.BaseFragment
import ar.com.play2play.presentation.base.BaseViewModel

/**
 * Base implementation of a [BaseGameFragment] used to simplify boilerplate.
 * It is a [BaseFragment] with base content of game screen
 * [GVB]: GameViewBinding
 * [E]: Event
 * [VM]: BaseViewModel with the same event defined
 */
abstract class BaseGameFragment<GVB : ViewBinding, E : Any, VM : BaseViewModel<E>, GVM : GameViewModel> :
    BaseFragment<BaseGameBinding, E, VM>() {

    protected abstract val gameViewModel: GVM

    protected open val isHeaderVisible = true

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

    @CallSuper
    override fun initUI() {
        binding.header.isVisible = isHeaderVisible
        setHeaderEvents(binding.header)
    }

    @CallSuper
    override fun setupObservers() {
        super.setupObservers()
        observe(gameViewModel.game) { binding.header.title = context?.getText(it.nameRes) }
        observe(gameViewModel.isBackAllowed) {
            binding.header.navigationIcon =
                if (it) ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24) else null
        }
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
