package com.p2p.presentation.tuttifrutti.create.rounds

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentRoundsNumberBinding
import com.p2p.presentation.base.BaseDialogFragment

class RoundsNumberFragment :
    BaseDialogFragment<FragmentRoundsNumberBinding, RoundsNumberEvent, RoundsNumberViewModel>() {

    override val viewModel: RoundsNumberViewModel by viewModels()
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRoundsNumberBinding =
        FragmentRoundsNumberBinding::inflate

    override fun initUI() {
        // Set dialog size on screen
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.4).toInt()
        dialog?.window?.setLayout(width, height);

        binding.arrowRight.setOnClickListener { viewModel.increase() }
        binding.arrowLeft.setOnClickListener { viewModel.decrease() }
        binding.createButton.setOnClickListener { viewModel.continueCreatingGame() }
    }

    override fun setupObservers() = with(viewModel) {
        roundsNumber.observe(viewLifecycleOwner) { binding.number.text = it.toString() }
    }

    override fun onEvent(event: RoundsNumberEvent) = when (event) {
        GoToTuttiFruttiLobby -> TODO()
    }

    companion object {
        /** Create a new instance of the [RoundsNumberFragment]. */
        fun newInstance() = RoundsNumberFragment()
    }
}
