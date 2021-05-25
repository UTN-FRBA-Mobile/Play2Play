package com.p2p.presentation.home.join

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.p2p.databinding.FragmentJoinGamesBinding
import com.p2p.framework.bluetooth.BluetoothDeviceFinderReceiver
import com.p2p.presentation.base.BaseBottomSheetDialogFragment

class JoinGamesBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentJoinGamesBinding, Unit, JoinGamesViewModel>() {

    private lateinit var adapter: ListDevicesAdapter
    private val receiver by lazy { BluetoothDeviceFinderReceiver() }

    override val viewModel: JoinGamesViewModel by viewModels { JoinGamesViewModelFactory(receiver) }
    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentJoinGamesBinding =
        FragmentJoinGamesBinding::inflate

    override fun initUI() {
        setupRecycler()
        binding.connectButton.setOnClickListener { viewModel.connect() }
    }

    override fun setupObservers() = with(viewModel) {
        devices.observe(viewLifecycleOwner) { adapter.devices = it }
        connectButtonEnabled.observe(viewLifecycleOwner) { binding.connectButton.isEnabled = it }
    }

    private fun setupRecycler() = with(binding.devicesRecycler) {
        layoutManager = LinearLayoutManager(context)
        adapter = ListDevicesAdapter(onSelectedChanged = viewModel::selectDevice).also {
            this@JoinGamesBottomSheetFragment.adapter = it
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val dialog = it as BottomSheetDialog
                val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                    ?: return@setOnShowListener
                BottomSheetBehavior.from(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    addBottomSheetCallback(object : BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                                state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        }
                    })
                }
            }
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
