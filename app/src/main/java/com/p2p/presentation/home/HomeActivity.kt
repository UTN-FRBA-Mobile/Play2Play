package com.p2p.presentation.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.p2p.R
import com.p2p.bluetooth.ClientBluetooth
import com.p2p.bluetooth.MESSAGE_READ
import com.p2p.bluetooth.MESSAGE_TOAST
import com.p2p.bluetooth.MESSAGE_WRITE
import com.p2p.bluetooth.MyBluetoothService
import com.p2p.bluetooth.ServerBluetooth
import com.p2p.presentation.base.BaseActivity


class HomeActivity : BaseActivity() {

    var connectedThread: MyBluetoothService.ConnectedThread? = null
    val handler by lazy {
        Handler(Looper.getMainLooper(), Handler.Callback {
            when (it.what) {
                MESSAGE_READ -> {
                    Log.d("DylanLog", "Message read: ${String(it.obj as ByteArray)}")
                    true
                }
                MESSAGE_WRITE -> {
                    Log.d("DylanLog", "Message write: ${String(it.obj as ByteArray)}")
                    true
                }
                MESSAGE_TOAST -> {
                    runOnUiThread { Toast.makeText(baseContext, String(it.obj as ByteArray), Toast.LENGTH_LONG).show() }
                    true
                }
                else -> false
            }
        })
    }

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action ?: return
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    Log.d("DylanLog", "$deviceName -> $deviceHardwareAddress")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ), REQUEST_LOCATION
            )
        }
        if (savedInstanceState == null) {
            //addFragment(GamesFragment.newInstance(), shouldAddToBackStack = true)
            //window.setBackgroundDrawableResource(R.color.colorBackground)
        }

        findViewById<View>(R.id.create).setOnClickListener { createServer() }
        findViewById<View>(R.id.connect).setOnClickListener { startDiscovery() }
        findViewById<View>(R.id.send).setOnClickListener {
            connectedThread?.write(
                findViewById<EditText>(R.id.message).text?.toString().orEmpty().toByteArray()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }

    private fun createServer() {
        ServerBluetooth().init(this)
    }

    private fun startDiscovery() {
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        bluetoothAdapter?.bondedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            if (deviceName == "Server 7 places available Moto E (4) Plus") {
                val uuid = findViewById<EditText>(R.id.input).text?.toString().orEmpty()
                ClientBluetooth().init(this, device, uuid)
            }
            Log.d("DylanLog", "Paired: $deviceName -> $deviceHardwareAddress")
        }
        bluetoothAdapter?.startDiscovery()
    }

    companion object {
        private const val REQUEST_LOCATION = 9001
    }
}
