package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import com.p2p.framework.SharedPreferencesUserInfoStorage
import com.p2p.framework.bluetooth.BluetoothConnectionCreatorImp
import com.p2p.framework.bluetooth.basemessage.HandshakeMessage
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.base.GameConnectionType
import com.p2p.presentation.tuttifrutti.create.CreateTuttiFruttiFragment

class TuttiFruttiActivity : BaseActivity() {

    private val bluetoothConnectionCreator = BluetoothConnectionCreatorImp(Looper.getMainLooper())
    private val userInfoRepository by lazy { SharedPreferencesUserInfoStorage(baseContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            // TODO: this logic shouldn't be here.
            val fragment = when (intent.getStringExtra(GameConnectionType.EXTRA)) {
                GameConnectionType.SERVER -> {
                    bluetoothConnectionCreator.createServer()
                    CreateTuttiFruttiFragment.newInstance()
                }
                GameConnectionType.CLIENT -> {
                    val device = requireNotNull(intent.getParcelableExtra<BluetoothDevice>(SERVER_DEVICE_EXTRA)) {
                        "A bluetooth device should be passed on the activity creation"
                    }
                    bluetoothConnectionCreator.createClient(device).onConnected { // TODO: should wait until connected to continue any processing
                        it.write(HandshakeMessage(userInfoRepository.getUserName() ?: "No tengo nombre :(")) // TODO: border case
                    }
                    return // TODO: Return lobby fragment
                }
                else -> return
            }
            addFragment(fragment, shouldAddToBackStack = false)
        }
    }

    companion object {

        private const val SERVER_DEVICE_EXTRA = "SERVER_DEVICE_EXTRA"

        fun startCreate(context: Context) {
            context.startActivity(Intent(context, TuttiFruttiActivity::class.java).apply {
                putExtra(GameConnectionType.EXTRA, GameConnectionType.SERVER)
            })
        }

        fun startJoin(context: Context, serverDevice: BluetoothDevice) {
            context.startActivity(Intent(context, TuttiFruttiActivity::class.java).apply {
                putExtra(GameConnectionType.EXTRA, GameConnectionType.CLIENT)
                putExtra(SERVER_DEVICE_EXTRA, serverDevice)
            })
        }
    }
}
