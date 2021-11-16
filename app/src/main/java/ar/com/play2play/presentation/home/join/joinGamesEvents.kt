package ar.com.play2play.presentation.home.join

import android.bluetooth.BluetoothDevice

sealed class JoinGamesEvent

object GoToHowToConnectBluetooth : JoinGamesEvent()

data class JoinGame(val device: BluetoothDevice) : JoinGamesEvent()
