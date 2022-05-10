package com.example.robotcontroller.joystick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.robotcontroller.bluetooth.BluetoothCommunicationService
import com.example.robotcontroller.bluetooth.MicroFRIMessageComposer

class JoystickActivity: AppCompatActivity() {

    private lateinit var mFriMessageComposer: MicroFRIMessageComposer
    private lateinit var mBluetoothCommunicationService: BluetoothCommunicationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFriMessageComposer = MicroFRIMessageComposer()
        mBluetoothCommunicationService = BluetoothCommunicationService(this, mFriMessageComposer)
        mBluetoothCommunicationService.setBluetoothConnection()

        val joystickView = JoystickView(this, null, mFriMessageComposer, mBluetoothCommunicationService)
        setContentView(joystickView)
        //setContentView(R.layout.activity_joystick)
    }

    override fun onStart() {
        super.onStart()
        mBluetoothCommunicationService.createBluetoothHandler(this.intent)
    }

    override fun onResume() {
        super.onResume()
        mBluetoothCommunicationService.startBluetooth()
    }

    override fun onDestroy() {
        mBluetoothCommunicationService.stopBluetooth()
        super.onDestroy()
    }
}