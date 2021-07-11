package com.p2p.presentation.impostor.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentCreateImpostorBinding
import com.p2p.databinding.FragmentCreateTuttiFruttiBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.impostor.ImpostorSpecificGameEvent
import com.p2p.presentation.impostor.ImpostorViewModel
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.create.rounds.TuttiFruttiRoundsNumberFragment

class CreateImpostorFragment : BaseGameFragment<
        FragmentCreateImpostorBinding,
        ImpostorSpecificGameEvent,
        CreateImpostorViewModel,
        ImpostorViewModel>() {

    override val gameViewModel: ImpostorViewModel by activityViewModels()
    override val viewModel: CreateImpostorViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateImpostorBinding =
        FragmentCreateImpostorBinding::inflate

    override fun initUI() {
        super.initUI()
        gameBinding.continueButton.setOnClickListener { gameViewModel.startGame(gameBinding.keyWordText.text.toString()) }
    }

    companion object {

        /** Create a new instance of the [CreateImpostorFragment]. */
        fun newInstance() = CreateImpostorFragment()
    }
}