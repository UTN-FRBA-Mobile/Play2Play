package com.p2p.presentation.tuttifrutti

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.p2p.data.instructions.InstructionsRepository
import com.p2p.framework.InstructionsLocalResourcesSource
import com.p2p.presentation.base.BaseActivity
import com.p2p.presentation.base.GameConnectionType
import com.p2p.presentation.home.games.Game
import com.p2p.presentation.tuttifrutti.create.categories.CreateTuttiFruttiFragment

class TuttiFruttiActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
             val instructions =
                InstructionsRepository(InstructionsLocalResourcesSource(applicationContext)).getInstructions(
                    Game.TUTTI_FRUTTI
                )

            addFragment(
                CreateTuttiFruttiFragment.newInstance(instructions),
                shouldAddToBackStack = true
            )
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
