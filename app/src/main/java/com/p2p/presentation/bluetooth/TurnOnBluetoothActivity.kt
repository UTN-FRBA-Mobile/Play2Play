package com.p2p.presentation.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.p2p.R
import com.p2p.presentation.base.BaseActivity

class TurnOnBluetoothActivity : BaseActivity(R.layout.activity_turn_on_bluetooth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.turn_on_button).setOnClickListener {
            BluetoothAdapter.getDefaultAdapter().enable()
            finish()
        }
    }

    companion object {

        fun start(context: Context) = context.startActivity(Intent(context, TurnOnBluetoothActivity::class.java))
    }
}
