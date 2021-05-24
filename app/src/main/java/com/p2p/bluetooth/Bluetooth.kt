package com.p2p.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.annotation.CallSuper
import com.p2p.presentation.home.HomeActivity
import java.lang.ref.WeakReference

abstract class Bluetooth {

    protected val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var activity: WeakReference<HomeActivity>? = null

    @CallSuper
    open fun init(activity: HomeActivity) {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        this.activity = WeakReference(activity)
    }

    fun manageMyConnectedSocket(bluetoothSocket: BluetoothSocket): BluetoothConnectionThread? {
        val activity = activity?.get() ?: return null
        return BluetoothConnectionThread(activity, bluetoothSocket)
    }

    abstract fun write(byteArray: ByteArray, offset: Int, length: Int)

    companion object {

        private const val REQUEST_ENABLE_BT = 1001
    }
}