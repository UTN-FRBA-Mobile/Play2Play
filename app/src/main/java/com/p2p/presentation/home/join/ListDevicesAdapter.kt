package com.p2p.presentation.home.join

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.p2p.R
import com.p2p.databinding.ViewDevicesItemBinding

/** The adapter used to show the list of available devices. */
class ListDevicesAdapter(private val onSelectedChanged: (BluetoothDevice?) -> Unit) :
    RecyclerView.Adapter<ListDevicesAdapter.ViewHolder>() {

    /** The list of devices displayed on the recycler. */
    var devices = listOf<BluetoothDevice>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /** The current selected device on the list. */
    var selected: BluetoothDevice? = null
        private set(value) {
            if (field == value) return
            field = value
            onSelectedChanged.invoke(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewDevicesItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(devices[position])

    override fun getItemCount() = devices.size

    inner class ViewHolder(private val binding: ViewDevicesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Show the given [device] into the view. */
        fun bind(device: BluetoothDevice) = with(binding) {
            container.setOnClickListener { selected = device }
            val colorRes = if (selected == device) R.color.gray else R.color.colorBackground
            container.setBackgroundColor(ContextCompat.getColor(root.context, colorRes))
            name.text = device.name
        }
    }
}
