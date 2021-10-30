package com.p2p.presentation.truco.create.points

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentCreateTrucoPointsBinding
import com.p2p.presentation.base.BaseDialogFragment
import com.p2p.presentation.truco.TrucoViewModel

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
