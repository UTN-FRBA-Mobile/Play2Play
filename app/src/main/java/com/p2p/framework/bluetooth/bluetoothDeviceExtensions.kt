package com.p2p.framework.bluetooth

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice

fun BluetoothDevice.isPhone() = bluetoothClass?.majorDeviceClass == BluetoothClass.Device.Major.PHONE
