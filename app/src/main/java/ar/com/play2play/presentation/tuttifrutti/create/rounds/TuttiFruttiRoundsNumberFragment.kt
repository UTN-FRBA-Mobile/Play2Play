package ar.com.play2play.presentation.tuttifrutti.create.rounds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ar.com.play2play.databinding.FragmentRoundsNumberBinding
import ar.com.play2play.presentation.base.BaseDialogFragment
import ar.com.play2play.presentation.tuttifrutti.TuttiFruttiViewModel

class TuttiFruttiRoundsNumberFragment :
    BaseDialogFragment<FragmentRoundsNumberBinding, Unit, RoundsNumberViewModel>() {

    override val viewModel: RoundsNumberViewModel by viewModels()

    private val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRoundsNumberBinding =
        FragmentRoundsNumberBinding::inflate

    override fun initUI() {
        binding.arrowRight.setOnClickListener { viewModel.increase() }
        binding.arrowLeft.setOnClickListener { viewModel.decrease() }
        binding.createButton.setOnClickListener {
            gameViewModel.setTotalRounds(binding.number.text.toString().toInt())
            gameViewModel.goToLobby()
        }
    }

    override fun setupObservers() =
        observe(viewModel.roundsNumber) {
            binding.number.text = it.toString()
        }

    companion object {

        /** Create a new instance of the [TuttiFruttiRoundsNumberFragment]. */
        fun newInstance() = TuttiFruttiRoundsNumberFragment()
    }
}
