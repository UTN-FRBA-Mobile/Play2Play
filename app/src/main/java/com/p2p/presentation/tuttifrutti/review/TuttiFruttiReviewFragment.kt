package com.p2p.presentation.tuttifrutti.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.p2p.databinding.FragmentReviewTuttiFruttiBinding
import com.p2p.databinding.PlayCategoryItemBinding
import com.p2p.model.tuttifrutti.FinishedRoundInfo
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.play.PlayTuttiFruttiFragment
import com.p2p.presentation.tuttifrutti.play.PlayTuttiFruttiViewModel
import com.p2p.utils.text

class TuttiFruttiReviewFragment : BaseGameFragment<
        FragmentReviewTuttiFruttiBinding,
        TuttiFruttiReviewEvents,
        TuttiFruttiReviewViewModel,
        TuttiFruttiViewModel>() {

    override val gameViewModel: TuttiFruttiViewModel by activityViewModels()

    override val viewModel: TuttiFruttiReviewViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReviewTuttiFruttiBinding =
        FragmentReviewTuttiFruttiBinding::inflate

    override fun initValues() { //TODO
    }

    override fun initUI() {
        super.initUI()
        // TODO
        }
    }




    companion object {

        /** Create a new instance of the [TuttiFruttiReviewFragment]. */
        fun newInstance() = TuttiFruttiReviewFragment()

    }
}