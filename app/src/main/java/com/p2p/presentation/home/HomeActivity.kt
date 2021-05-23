package com.p2p.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_P2P_INFO
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.p2p.R
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.base.BaseViewModel


class HomeActivity : BaseActivity() {

    private val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    var channel: WifiP2pManager.Channel? = null
    var receiver: WiFiDirectBroadcastReceiver? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!initP2P()) {
            showSnackBar(
                BaseViewModel.MessageData(
                    text = "Cannot use the application :(",
                    type = BaseViewModel.MessageData.Type.ERROR
                )
            )
        }
        findViewById<Button>(R.id.connect).setOnClickListener { discoverPeers() }
        findViewById<Button>(R.id.create).setOnClickListener { discoverPeers() }
        askForLocationPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION -> {
                if (grantResults.first() != PackageManager.PERMISSION_GRANTED) {
                    Log.e("WiFiDirectActivity", "Fine location permission is not granted!")
                    finish()
                } else {
                    //discoverPeers()
                }
            }
        }
    }

    /* register the broadcast receiver with the intent values to be matched */
    override fun onResume() {
        super.onResume()
        receiver?.also { receiver -> registerReceiver(receiver, intentFilter) }
    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()
        receiver?.also { receiver -> unregisterReceiver(receiver) }
    }

    private fun initP2P(): Boolean {
        // Device capability definition check
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.")
            return false
        }
        // Hardware capability check
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        if (wifiManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi system service.")
            return false
        }
        if (!wifiManager.isP2pSupported) {
            Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.")
            return false
        }
        val manager = manager
        if (manager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.")
            return false
        }
        val channel = manager.initialize(this, mainLooper, null)
        this.channel = channel
        if (channel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.")
            return false
        }
        receiver = WiFiDirectBroadcastReceiver(manager, channel, this)
        return true
    }

    private fun askForLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
            )
        } else {
            //discoverPeers()
        }
    }

    @SuppressLint("MissingPermission") // This is because we're already calling this method after verifying permission.
    private fun discoverPeers() {
        Log.d("DylanLog", "Request peers")
        receiver?.isSearchingPeers = true
        manager?.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                    override fun onSuccess() {
                        Log.d("DylanLog", "Success discover")
                    }

                    override fun onFailure(reason: Int) {
                        Log.d("DylanLog", "Cannot discover: $reason")
                    }
                })
            }

            override fun onFailure(reason: Int) {
                manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                    override fun onSuccess() {
                        Log.d("DylanLog", "Success discover")
                    }

                    override fun onFailure(reason: Int) {
                        Log.d("DylanLog", "Cannot discover: $reason")
                    }
                })
            }
        })
    }

    /** Show a snackbar customized with the given [data]. */
    fun showSnackBar(data: BaseViewModel.MessageData) {
        val text = data.textRes?.let { baseContext.getText(it) } ?: data.text ?: return
        val duration = when (data.duration) {
            BaseViewModel.MessageData.Duration.SHORT -> Snackbar.LENGTH_SHORT
            BaseViewModel.MessageData.Duration.LONG -> Snackbar.LENGTH_LONG
        }
        val backgroundColor = when (data.type) {
            BaseViewModel.MessageData.Type.ERROR -> R.color.design_default_color_error
        }
        Snackbar
            .make(findViewById(android.R.id.content), text, duration)
            .setBackgroundTint(ContextCompat.getColor(baseContext, backgroundColor))
            .show()
    }

    companion object {

        private const val TAG = "DylanLog"
        private const val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001
    }
}

class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: HomeActivity
) : BroadcastReceiver() {

    var isSearchingPeers = false
    var alreadyConnected = false

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                when (state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        Log.d("DylanLog", "Wifi P2P enabled")
                        // Wifi P2P is enabled
                    }
                    else -> activity.showSnackBar(
                        BaseViewModel.MessageData(
                            text = "Conectá el dispositivo a WiFi P2P!",
                            type = BaseViewModel.MessageData.Type.ERROR
                        )
                    )
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (!isSearchingPeers) return
                manager.requestPeers(channel) {
                    Log.d("DylanLog", "Peers: ${it.deviceList.joinToString { it.deviceName }}")
                    val device = it.deviceList.firstOrNull { device ->
                        device.deviceName in listOf("Moto E (4) Plus_dbba", "Mi A3")
                    } ?: return@requestPeers
                    val deviceName = device.deviceName
                    val config = WifiP2pConfig().apply {
                        deviceAddress = device.deviceAddress
                        groupOwnerIntent = if (device.deviceName == "Moto E (4) Plus_dbba") 0 else 15
                    }
                    isSearchingPeers = false
                    channel.also { channel ->
                        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
                            override fun onSuccess() {
                                Log.d("DylanLog", "Connected to $deviceName")
                                //val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                                //if (connectivityManager?.isConnected(context) != true) return

                                manager?.requestConnectionInfo(channel) {
                                    Log.d("DylanLog", "We have the connection info: $it")
                                    if (it.groupFormed) {

                                    }
                                }
                            }

                            override fun onFailure(reason: Int) {
                                Log.d("DylanLog", "Cannot connect to $deviceName :(")
                            }

                        })
                    }
                    Log.d(
                        "DylanLog",
                        it.deviceList.joinToString(";;;;;;;;;;") { "${it.deviceName}: ${it.deviceAddress}, ${it.primaryDeviceType}, ${it.secondaryDeviceType}, ${it.status}, ${it.isGroupOwner}" })
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections

                // Check if it's still connected to network
                Log.d(
                    "DylanLog",
                    "Device connected: ${intent.getSerializableExtra(EXTRA_WIFI_P2P_INFO) as? WifiP2pInfo}"
                )
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }
    }
}
