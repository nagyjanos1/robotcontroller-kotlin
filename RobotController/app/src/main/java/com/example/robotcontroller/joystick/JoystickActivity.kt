package com.example.robotcontroller.joystick

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.robotcontroller.MainActivity
import com.example.robotcontroller.MainActivity.Companion.REQUEST_ENABLE_BLUETOOTH
import java.io.IOException
import java.util.*

class JoystickActivity: AppCompatActivity() {
    private lateinit var mUUID: UUID

    private var bSocketService: BluetoothDataSharingService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        val isConnected = connectToDevice()
        if (isConnected) {
            val joystickView = JoystickView(this, null, bSocketService)
            setContentView(joystickView)
        } else {
            setContentView(R.layout.activity_joystick)
        }
    }

    private fun connectToDevice(): Boolean {
        val bAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!bAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth adapter is not enabled.", Toast.LENGTH_LONG).show()
            return
        }

        val address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)
        if (address.isNullOrEmpty()) {
            Toast.makeText(this, "Address is not available.", Toast.LENGTH_LONG).show()
            return
        }

        val device = bAdapter.getRemoteDevice(address)
        var bSocket: BluetoothSocket? = null

        var isConnected = false
        try {
            val checkSelfPermissionResult = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT)
            if (checkSelfPermissionResult != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {
                    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)

                    Toast.makeText(this, "Bluetooth is not enabled.", Toast.LENGTH_LONG).show()
                }
                return
            }

            bSocket = device.createRfcommSocketToServiceRecord(mUUID)
            bSocketService = BluetoothDataSharingService(bSocket)
            isConnected = bSocketService?.connect() == true
            if (isConnected) {
                Toast.makeText(this, "Bluetooth socket is created.", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return isConnected
    }

    override fun onDestroy() {
        bSocketService?.close()

        super.onDestroy()
    }
}