package com.p2p.presentation.tuttifrutti.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.p2p.databinding.FragmentReviewTuttiFruttiBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

class ReviewTuttiFruttiFragment : BaseGameFragment<
        FragmentReviewTuttiFruttiBinding,
        TuttiFruttiReviewEvents,
        ReviewTuttiFruttiViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: ReviewTuttiFruttiViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReviewTuttiFruttiBinding =
        FragmentReviewTuttiFruttiBinding::inflate

    override fun initValues() { //TODO
    }

    override fun initUI() {
        super.initUI()
        //TODO
    }
}