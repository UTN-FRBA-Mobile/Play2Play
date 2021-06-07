package com.p2p.presentation.tuttifrutti.review

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.p2p.model.tuttifrutti.FinishedRoundInfo

class TuttiFruttiReviewFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("P2P_REVIEW", arguments
            ?.getParcelableArray(FINISHED_ROUND_INFO_EXTRA)
            ?.toList()
            ?.filterIsInstance<FinishedRoundInfo>()
            ?.joinToString("\n\n") { "${it.player} --> ${it.categoriesWords}" } ?: "No arguments")
    }

    companion object {

        private const val FINISHED_ROUND_INFO_EXTRA = "FINISHED_ROUND_INFO_EXTRA"

        fun newInstance(finishedRoundInfo: List<FinishedRoundInfo>) = TuttiFruttiReviewFragment().apply {
            arguments = bundleOf(FINISHED_ROUND_INFO_EXTRA to finishedRoundInfo.toTypedArray())
        }
    }
}
