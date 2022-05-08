package com.example.robotcontroller.joystick

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.robotcontroller.R
import com.example.robotcontroller.bluetooth.BluetoothHandlerService
import com.example.robotcontroller.bluetooth.MicroFRIHandler

class JoystickActivity: AppCompatActivity() {

    private lateinit var mFriHandler: MicroFRIHandler
    private lateinit var mBluetoothHandlerService: BluetoothHandlerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFriHandler = MicroFRIHandler()
        mBluetoothHandlerService = BluetoothHandlerService(this, mFriHandler)
        mBluetoothHandlerService.setBluetoothConnection()

        val joystickView = JoystickView(this, null, mFriHandler, mBluetoothHandlerService)
        setContentView(joystickView)
        //setContentView(R.layout.activity_joystick)
    }

    override fun onStart() {
        super.onStart()
        mBluetoothHandlerService.createBluetoothHandler(this.intent)
    }

    override fun onResume() {
        super.onResume()
        mBluetoothHandlerService.startBluetooth()
    }

    override fun onDestroy() {
        mBluetoothHandlerService.stopBluetooth()
        super.onDestroy()
    }
}