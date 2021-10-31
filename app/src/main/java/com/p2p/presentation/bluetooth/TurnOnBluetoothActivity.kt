package com.p2p.presentation.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.p2p.R
import com.p2p.presentation.base.BaseActivity

class TurnOnBluetoothActivity : BaseActivity(R.layout.activity_turn_on_bluetooth) {

    private val receiver = ActiveBluetoothBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.turn_on_button).setOnClickListener {
            findViewById<Button>(R.id.turn_on_button).text = null
            findViewById<View>(R.id.loading).isVisible = true
            BluetoothAdapter.getDefaultAdapter().enable()
        }
    }

    override fun onResume() {
        super.onResume()
        if (BluetoothAdapter.getDefaultAdapter().isEnabled) {
            setResult(Activity.RESULT_OK)
            finish()
            return
        }
        registerReceiver(receiver, receiver.intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    inner class ActiveBluetoothBroadcastReceiver : BroadcastReceiver() {

        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action ?: return
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                if (state == BluetoothAdapter.STATE_ON) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    companion object {

        fun startForResult(fragment: Fragment, requestCode: Int) = fragment.startActivityForResult(
            Intent(fragment.requireContext(), TurnOnBluetoothActivity::class.java),
            requestCode
        )
    }
}
