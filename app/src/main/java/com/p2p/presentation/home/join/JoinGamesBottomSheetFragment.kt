package com.p2p.presentation.home.join

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.p2p.databinding.FragmentJoinGamesBinding
import com.p2p.framework.bluetooth.BluetoothDeviceFinderReceiver
import com.p2p.presentation.base.BaseBottomSheetDialogFragment
import com.p2p.presentation.bluetooth.HowToConnectBluetoothActivity
import com.p2p.presentation.home.HomeActivity.Companion.GAME_REQUEST_CODE
import com.p2p.presentation.tuttifrutti.TuttiFruttiActivity

class JoinGamesBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentJoinGamesBinding, JoinGamesEvent, JoinGamesViewModel>() {

    private lateinit var adapter: ListDevicesAdapter
    private val receiver by lazy { BluetoothDeviceFinderReceiver() }

    override val viewModel: JoinGamesViewModel by viewModels { JoinGamesViewModelFactory(receiver) }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentJoinGamesBinding =
        FragmentJoinGamesBinding::inflate
    override val isCollapsable = false

    override fun initUI() {
        setupRecycler()
        binding.connectButton.setOnClickListener { viewModel.connect() }
        binding.troubleshoot.setOnClickListener { viewModel.troubleshoot() }
    }

    override fun setupObservers() = with(viewModel) {
        observe(devices) { adapter.devices = it.toList() }
        observe(connectButtonEnabled) { binding.connectButton.isEnabled = it }
    }

    override fun onEvent(event: JoinGamesEvent) = when (event) {
        GoToHowToConnectBluetooth -> {
            dismiss()
            HowToConnectBluetoothActivity.start(requireContext())
        }
        is JoinGame -> TuttiFruttiActivity.startJoin(requireActivity(), GAME_REQUEST_CODE, event.device)
    }

    private fun setupRecycler() = with(binding.devicesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ListDevicesAdapter(onSelectedChanged = viewModel::selectDevice).also {
            this@JoinGamesBottomSheetFragment.adapter = it
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.registerReceiver(receiver, receiver.intentFilter)
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(receiver)
    }

    companion object {

        fun newInstance() = JoinGamesBottomSheetFragment()
    }
}
