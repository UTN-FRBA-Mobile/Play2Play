package com.p2p.presentation.home.join

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentJoinGamesBinding
import com.p2p.presentation.base.BaseBottomSheetDialogFragment

class JoinGamesBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentJoinGamesBinding, Unit, JoinGamesViewModel>() {

    override val viewModel: JoinGamesViewModel by viewModels()
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentJoinGamesBinding =
        FragmentJoinGamesBinding::inflate


}