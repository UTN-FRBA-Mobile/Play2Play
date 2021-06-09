package com.p2p.presentation.tuttifrutti.review

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.p2p.R
import com.p2p.model.tuttifrutti.FinishedRoundInfo

class TuttiFruttiReviewFragment : Fragment() {

    // TODO: Clean this
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val finishedRoundInfo = requireNotNull(
            arguments
                ?.getParcelableArrayList<FinishedRoundInfo>(FINISHED_ROUND_INFO_EXTRA)
                ?.toList()
        ) {
            "No FinishedRoundInfo sent. Use the TuttiFruttiReviewFragment.newInstance."
        }
        Log.d(
            "P2P_REVIEW",
            finishedRoundInfo.joinToString("\n\n") { "${it.player} --> ${it.categoriesWords}" }
        )
        // Delete this! It's awful and we should neve do this.
        activity?.findViewById<View>(R.id.activity_progress_overlay)?.isVisible = false
    }

    companion object {

        private const val FINISHED_ROUND_INFO_EXTRA = "FINISHED_ROUND_INFO_EXTRA"

        fun newInstance(finishedRoundInfo: List<FinishedRoundInfo>) = TuttiFruttiReviewFragment().apply {
            arguments = bundleOf(FINISHED_ROUND_INFO_EXTRA to finishedRoundInfo.toTypedArray())
        }
    }
}
