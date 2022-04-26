package com.example.robotcontroller.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.robotcontroller.MainActivity

class BluetoothBroadcastReceiver(activity: MainActivity) : BroadcastReceiver() {
    private var _activity: MainActivity = activity

    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action;
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            val bluetoothStatus = p1?.getIntExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR
            )
            bluetoothStatus?.let {
                _activity.setBluetoothStatusOnDisplay(it)
            }
        }
    }

}