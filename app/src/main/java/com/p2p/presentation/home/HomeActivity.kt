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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.p2p.R
import com.p2p.bluetooth.Bluetooth
import com.p2p.bluetooth.BluetoothConnectionThread
import com.p2p.bluetooth.BluetoothConnectionThread.Companion.MESSAGE_READ
import com.p2p.bluetooth.BluetoothConnectionThread.Companion.MESSAGE_TOAST
import com.p2p.bluetooth.BluetoothConnectionThread.Companion.MESSAGE_WRITE
import com.p2p.bluetooth.ClientBluetooth
import com.p2p.bluetooth.ServerBluetooth
import com.p2p.framework.Logger
import com.p2p.presentation.base.BaseActivity


class HomeActivity : BaseActivity() {

    private lateinit var messages: TextView
    private var connectedThread: Bluetooth? = null
    val handler by lazy {
        Handler(Looper.getMainLooper()) {
            when (it.what) {
                MESSAGE_READ -> {
                    val message = String(it.obj as ByteArray, 0, it.arg1)
                    Logger.d("P2P_DylanLog", "Message read: $message")
                    runOnUiThread { messages.append("\nMessage read: $message") }
                    true
                }
                MESSAGE_WRITE -> {
                    val message = String(it.obj as ByteArray, 0, it.arg1)
                    Logger.d("P2P_DylanLog", "Message write: $message")
                    runOnUiThread { messages.append("\nMessage write: $message") }
                    true
                }
                MESSAGE_TOAST -> {
                    runOnUiThread {
                        Toast.makeText(
                            baseContext,
                            String(it.obj as ByteArray, 0, it.arg1),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    true
                }
                else -> false
            }
        }
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
                    Log.d("P2P_DylanLog", "$deviceName -> $deviceHardwareAddress")
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

        messages = findViewById(R.id.messages)
        findViewById<View>(R.id.create).setOnClickListener { createServer() }
        findViewById<View>(R.id.connect).setOnClickListener { startDiscovery() }
        findViewById<View>(R.id.send).setOnClickListener {
            val message = findViewById<EditText>(R.id.message).text?.toString().orEmpty()
            connectedThread?.write(message.toByteArray(), 0, message.length)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }

    private fun createServer() {
        connectedThread = ServerBluetooth().apply {
            init(this@HomeActivity)
        }
    }

    private fun startDiscovery() {
        Log.d("P2P_DylanLog", "Start discovery")
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        bluetoothAdapter?.bondedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            Log.d("P2P_DylanLog", "Paired: $deviceName -> $deviceHardwareAddress")
            if (deviceName == "Server 7 places available Moto E (4) Plus") {
                connectedThread = ClientBluetooth().apply {
                    init(this@HomeActivity, device)
                }
            }
        }
        bluetoothAdapter?.startDiscovery()
    }

    companion object {
        private const val REQUEST_LOCATION = 9001
    }
}
