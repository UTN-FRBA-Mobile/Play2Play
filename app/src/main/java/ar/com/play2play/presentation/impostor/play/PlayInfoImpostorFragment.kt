package ar.com.play2play.presentation.impostor.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ar.com.play2play.databinding.FragmentPlayInfoImpostorBinding
import ar.com.play2play.presentation.basegame.BaseGameFragment
import ar.com.play2play.presentation.impostor.ImpostorViewModel

class PlayInfoImpostorFragment : BaseGameFragment<
        FragmentPlayInfoImpostorBinding,
        Unit,
        PlayImpostorViewModel,
        ImpostorViewModel>() {

    override val gameViewModel: ImpostorViewModel by activityViewModels()

    override val viewModel: PlayImpostorViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayInfoImpostorBinding =
        FragmentPlayInfoImpostorBinding::inflate

    override fun initUI() {
        super.initUI()
        gameBinding.finishButton.setOnClickListener {
            gameViewModel.endGame()
            requireActivity().finish()
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(impostorData) { data ->
                gameBinding.keyWord.text = data.keyWord
                gameBinding.keyWordTheme.text = data.keyWordTheme
                gameBinding.impostor.text = data.impostor
            }
        }
    }

    companion object {

        /** Create a new instance of the [PlayInfoImpostorFragment]. */
        fun newInstance() = PlayInfoImpostorFragment()
    }
}
