package ar.com.play2play.presentation.impostor.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ar.com.play2play.R
import ar.com.play2play.databinding.FragmentPlayImpostorBinding
import ar.com.play2play.presentation.basegame.BaseGameFragment
import ar.com.play2play.presentation.impostor.ImpostorViewModel

class PlayImpostorFragment : BaseGameFragment<
        FragmentPlayImpostorBinding,
        Unit,
        PlayImpostorViewModel,
        ImpostorViewModel>() {

    override val gameViewModel: ImpostorViewModel by activityViewModels()

    override val viewModel: PlayImpostorViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayImpostorBinding =
        FragmentPlayImpostorBinding::inflate

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(impostorData) { data ->
                setWordToShow(data.isImpostor, data.keyWord)
                setScreenDescription(data.isImpostor)
                setScreenIcon(data.isImpostor)
                gameBinding.keyWordTheme.text = data.keyWordTheme
            }
        }
    }

    private fun setScreenDescription(isImpostor: Boolean) = with(gameBinding) {
        val resource =
            if (isImpostor) R.string.im_you_are_impostor else R.string.im_you_are_not_impostor
        screenDescription.text = resources.getString(resource)
    }

    private fun setWordToShow(isImpostor: Boolean, keyWord: String) {
        val wordToShow = if (isImpostor) resources.getString(R.string.im_simulate) else keyWord
        gameBinding.word.text = wordToShow
    }

    private fun setScreenIcon(isImpostor: Boolean) {
        val resource = if (isImpostor) R.drawable.ic_emulate_impostor else R.drawable.ic_search_impostor
        gameBinding.taskImage.setImageResource(resource)
    }

    companion object {

        /** Create a new instance of the [PlayImpostorFragment]. */
        fun newInstance() = PlayImpostorFragment()
    }
}
