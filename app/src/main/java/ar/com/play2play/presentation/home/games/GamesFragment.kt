package ar.com.play2play.presentation.home.games

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ar.com.play2play.databinding.FragmentGamesBinding
import ar.com.play2play.framework.ApplicationSharer
import ar.com.play2play.presentation.base.BaseFragment
import ar.com.play2play.presentation.bluetooth.TurnOnBluetoothActivity
import ar.com.play2play.presentation.extensions.clearAndAppend
import ar.com.play2play.presentation.home.HomeActivity.Companion.GAME_REQUEST_CODE
import ar.com.play2play.presentation.home.join.JoinGamesBottomSheetFragment
import ar.com.play2play.presentation.impostor.ImpostorActivity
import ar.com.play2play.presentation.truco.TrucoActivity
import ar.com.play2play.presentation.tuttifrutti.TuttiFruttiActivity

class GamesFragment : BaseFragment<FragmentGamesBinding, GamesEvents, GamesViewModel>() {

    override val viewModel: GamesViewModel by viewModels { GamesViewModelFactory(requireContext()) }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGamesBinding =
        FragmentGamesBinding::inflate

    private lateinit var adapter: GamesAdapter

    override fun initUI() {
        setupGamesRecycler()
        binding.shareButton.setOnClickListener { ApplicationSharer.share(requireActivity()) }
    }

    override fun setupObservers() = with(viewModel) {
        observe(games) { adapter.games = it }
        observe(userName) { binding.userNameInput.clearAndAppend(it) }
    }

    override fun onEvent(event: GamesEvents) = when (event) {
        GoToCreateTuttiFrutti -> TuttiFruttiActivity.startCreate(requireActivity(), GAME_REQUEST_CODE)
        GoToCreateImpostor -> ImpostorActivity.startCreate(requireActivity(), GAME_REQUEST_CODE)
        GoToCreateTruco -> TrucoActivity.startCreate(requireActivity(), GAME_REQUEST_CODE)
        is JoinGame -> JoinGamesBottomSheetFragment.newInstance(event.game)
            .show(parentFragmentManager, null)
        TurnOnBluetooth -> TurnOnBluetoothActivity.startForResult(this, TURN_ON_BLUETOOTH_REQUEST_CODE)
    }

    private fun setupGamesRecycler() = with(binding.gamesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = GamesAdapter(
            onCreateClicked = { viewModel.createGame(it, getUserName()) },
            onJoinClicked = { viewModel.joinGame(it, getUserName()) }
        ).also {
            this@GamesFragment.adapter = it
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TURN_ON_BLUETOOTH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.onBluetoothTurnedOn(getUserName())
        }
    }

    private fun getUserName() = binding.userNameInput.text?.toString()

    companion object {

        private const val TURN_ON_BLUETOOTH_REQUEST_CODE = 9001

        /** Create a new instance of the [GamesFragment]. */
        fun newInstance() = GamesFragment()
    }
}