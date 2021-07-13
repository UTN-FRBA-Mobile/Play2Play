package com.p2p.presentation.impostor.play

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.p2p.R
import com.p2p.databinding.FragmentPlayImpostorBinding
import com.p2p.databinding.FragmentPlayTuttiFruttiBinding
import com.p2p.databinding.ViewPlayCategoryItemBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.basegame.GameEvent
import com.p2p.presentation.impostor.ImpostorViewModel
import com.p2p.presentation.tuttifrutti.ObtainWords
import com.p2p.presentation.tuttifrutti.TuttiFruttiViewModel
import com.p2p.presentation.tuttifrutti.create.categories.Category
import com.p2p.utils.fromHtml
import com.p2p.utils.text

class PlayImpostorFragment : BaseGameFragment<
        FragmentPlayImpostorBinding,
        Unit,
        PlayImpostorViewModel,
        ImpostorViewModel>() {

    override val gameViewModel: ImpostorViewModel by activityViewModels()

    override val viewModel: PlayImpostorViewModel by viewModels()

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayImpostorBinding =
        FragmentPlayImpostorBinding::inflate

    override fun initUI() {
        super.initUI()
        setScreenDescription()
    }

    private fun setScreenDescription() = with(gameBinding) {
        val resource =
            if (gameViewModel.isImpostor()) R.string.im_you_are_impostor else R.string.im_you_are_not_impostor
        screenDescription.text = resources.getString(resource)
    }

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(keyWord) { word ->
                val wordToShow =
                    if (gameViewModel.isImpostor())
                        resources.getString(R.string.im_simulate) else word
                gameBinding.word.text = wordToShow
            }
        }
    }

    companion object {

        /** Create a new instance of the [PlayImpostorFragment]. */
        fun newInstance() = PlayImpostorFragment()
    }
}