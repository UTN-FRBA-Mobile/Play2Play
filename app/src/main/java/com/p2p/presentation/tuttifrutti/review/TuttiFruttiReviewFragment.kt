package com.p2p.presentation.tuttifrutti.review

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.model.tuttifrutti.FinishedRoundInfo

class TuttiFruttiReviewFragment : Fragment() {

    // TODO: Clean this
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val finishedRoundInfo: List<FinishedRoundInfo> = requireNotNull(
            arguments?.getParcelableArrayList(FINISHED_ROUND_INFO_EXTRA)
        ) {
            "No FinishedRoundInfo sent. Use the TuttiFruttiReviewFragment.newInstance."
        }
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(finishedRoundInfo.joinToString("\n\n") { "${it.player} --> ${it.categoriesWords}" })
            .show()
        // Delete this! It's awful and we should neve do this.
        activity?.findViewById<View>(R.id.activity_progress_overlay)?.isVisible = false
    }

    companion object {

        private const val FINISHED_ROUND_INFO_EXTRA = "FINISHED_ROUND_INFO_EXTRA"

        fun newInstance(finishedRoundInfo: List<FinishedRoundInfo>) = TuttiFruttiReviewFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(FINISHED_ROUND_INFO_EXTRA, ArrayList(finishedRoundInfo))
            }
        }
    }
}
