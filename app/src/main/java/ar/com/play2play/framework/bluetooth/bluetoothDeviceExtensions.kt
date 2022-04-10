package ar.com.play2play.framework.bluetooth

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice

fun BluetoothDevice.isPhone() = bluetoothClass?.majorDeviceClass == BluetoothClass.Device.Major.PHONE
