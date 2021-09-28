package com.p2p.presentation.truco.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentCreateTrucoBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.truco.TrucoViewModel

class CreateTrucoFragment : BaseGameFragment<
        FragmentCreateTrucoBinding,
        CreateTrucoEvents,
        CreateTrucoViewModel,
        TrucoViewModel>() {

    override val gameViewModel: TrucoViewModel by activityViewModels()
    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateTrucoBinding =
        FragmentCreateTrucoBinding::inflate

    override val viewModel: CreateTrucoViewModel by viewModels()

    override fun initUI() {
        super.initUI()
        gameBinding.createTwoPlayer.setOnClickListener {
            viewModel.createLobby(2)
        }
        gameBinding.createFourPlayer.setOnClickListener {
            viewModel.createLobby(4)
        }
    }

    override fun onEvent(event: CreateTrucoEvents) = when(event) {
        is CreateTrucoLobbyEvent -> {
            gameViewModel.setTotalPlayers(event.numberOfPlayers)
            gameViewModel.goToLobby()
        }
    }

    companion object {
        /** Create a new instance of the [CreateTrucoFragment]. */
        fun newInstance() = CreateTrucoFragment()
    }
}