package com.p2p.presentation.impostor.play

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
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

    override fun setupObservers() {
        super.setupObservers()
        with(gameViewModel) {
            observe(impostorData) { data ->
                setWordToShow(data.isImpostor, data.keyWord)
                setScreenDescription(data.isImpostor)
                setScreenIcon(data.isImpostor)
            }
        }
    }

    private fun setScreenDescription(isImpostor: Boolean) = with(gameBinding) {
        val resource =
            if (isImpostor) R.string.im_you_are_impostor else R.string.im_you_are_not_impostor
        screenDescription.text = resources.getString(resource)
    }

    private fun setWordToShow(isImpostor: Boolean, keyWord: String) {
        val wordToShow = if (isImpostor) resources.getString(R.string.im_simulate) else keyWord
        gameBinding.word.text = wordToShow
    }

    private fun setScreenIcon(isImpostor: Boolean) {
        val resource = if (isImpostor) R.drawable.ic_impostor else R.drawable.ic_search_impostor
        gameBinding.taskImage.setImageResource(resource)
    }

    companion object {

        /** Create a new instance of the [PlayImpostorFragment]. */
        fun newInstance() = PlayImpostorFragment()
    }
}
