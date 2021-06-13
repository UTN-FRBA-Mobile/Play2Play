package com.p2p.presentation.tuttifrutti.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.p2p.R
import com.p2p.databinding.BaseGameBinding
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel

// TODO: clean this
class TuttiFruttiReviewFragment : Fragment() {

    val viewmodel: TuttiFruttiViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return BaseGameBinding.inflate(inflater).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Delete this! It's awful and we should neve do this.
        activity?.findViewById<View>(R.id.activity_progress_overlay)?.isVisible = false
        viewmodel.finishedRoundInfos.observe(viewLifecycleOwner) { finishedRoundInfo ->
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(finishedRoundInfo.joinToString("\n\n") {
                    "${it.player} ${if (it.saidEnough) "(dijo basta)" else ""} --> ${it.categoriesWords}"
                })
                .show()
        }
    }

    companion object {

        fun newInstance() = TuttiFruttiReviewFragment()
    }
}
