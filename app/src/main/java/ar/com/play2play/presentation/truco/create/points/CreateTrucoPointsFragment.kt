package ar.com.play2play.presentation.truco.create.points

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ar.com.play2play.databinding.FragmentCreateTrucoPointsBinding
import ar.com.play2play.presentation.base.BaseDialogFragment
import ar.com.play2play.presentation.truco.TrucoViewModel

class CreateTrucoPointsFragment :
    BaseDialogFragment<FragmentCreateTrucoPointsBinding, Unit, CreateTrucoPointsViewModel>() {

    override val viewModel: CreateTrucoPointsViewModel by viewModels()

    private val gameViewModel: TrucoViewModel by activityViewModels()

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTrucoPointsBinding =
        FragmentCreateTrucoPointsBinding::inflate

    override fun initUI() {
        binding.arrowLeft.isEnabled = true
        binding.arrowRight.isEnabled = false
        binding.arrowRight.setOnClickListener { viewModel.increase() }
        binding.arrowLeft.setOnClickListener { viewModel.decrease() }
        binding.createButton.setOnClickListener {
            gameViewModel.setTotalPoints(binding.number.text.toString().toInt())
            gameViewModel.goToLobby()
        }
    }

    override fun setupObservers() {
        observe(viewModel.pointsNumber) {
            binding.number.text = it.toString()
        }
        observe(viewModel.enableIncrease) {
            binding.arrowRight.isEnabled = it
            binding.arrowLeft.isEnabled = !it

        }
        observe(viewModel.enableDecrease) {
            binding.arrowLeft.isEnabled = it
            binding.arrowRight.isEnabled = !it
        }
    }


    companion object {

        /** Create a new instance of the [CreateTrucoPointsFragment]. */
        fun newInstance() = CreateTrucoPointsFragment()
    }
}
