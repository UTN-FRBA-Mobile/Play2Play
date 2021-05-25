package com.p2p.presentation.tuttifrutti.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.data.tuttifrutti.TuttiFruttiMetadata
import com.p2p.databinding.FragmentPlayTuttiFruttiBinding
import com.p2p.presentation.basegame.BaseGameFragment
import com.p2p.presentation.home.games.Game

class PlayTuttiFruttiFragment :
    BaseGameFragment<FragmentPlayTuttiFruttiBinding, TuttiFruttiPlayingEvents, PlayTuttiFruttiViewModel>() {

    override val viewModel: PlayTuttiFruttiViewModel by viewModels()


    override var instructions: String? = null

    override val gameInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayTuttiFruttiBinding =
        FragmentPlayTuttiFruttiBinding::inflate

    override val gameData = Game.TUTTI_FRUTTI


    private lateinit var tuttiFruttiCategoriesAdapter: TuttiFruttiWriteCategoriesAdapter

    override fun initUI() {
        super.initUI()
        setupCategoriesRecycler()
    }

    override fun initValues() {
        val arguments = requireArguments()
        instructions = arguments.getString(INSTRUCTIONS_KEY)!!
        viewModel.metadata = arguments.getParcelable(GAME_METADATA_KEY)!!
    }

    override fun setupObservers() = with(viewModel) {
        stopButtonEnabled.observe(viewLifecycleOwner) {
            gameBinding.finishRoundButton.isEnabled = it
        }
    }


    open override fun onEvent(event: TuttiFruttiPlayingEvents) = when (event) {
        //TODO end game for all
        EndRound -> {
        }
    }

    private fun setupCategoriesRecycler() = with(gameBinding.categoriesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter =
            TuttiFruttiWriteCategoriesAdapter(viewModel.metadata!!.categories!!, viewModel::onWrittenCategory).also {
                this@PlayTuttiFruttiFragment.tuttiFruttiCategoriesAdapter = it
            }
    }


    companion object {

        const val INSTRUCTIONS_KEY = "Instructions"
        const val GAME_METADATA_KEY = "GameMetadata"

        /** Create a new instance of the [PlayTuttiFruttiFragment]. */
        fun newInstance(instructions: String, tuttiFruttiMetadata: TuttiFruttiMetadata) =
            PlayTuttiFruttiFragment().apply {
                arguments = bundleOf(
                    INSTRUCTIONS_KEY to instructions,
                    GAME_METADATA_KEY to tuttiFruttiMetadata
                )
            }
    }
}