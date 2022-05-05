package com.example.robotcontroller.joystick

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.OutputStream

class MicroFRIHandler(private val bSocketService: BluetoothDataSharingService) {
    sendUniverseInitFrame() {

    }

    sendUniverse() {

    }
}

class BluetoothDataSharingService(private val bSocket: BluetoothSocket) {
    @SuppressLint("MissingPermission")
    fun connect(): Boolean {
        var counter = 0
        do
            try {
                counter++

                bSocket.connect()
                //Toast.makeText(this, "Bluetooth socket is connected.", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        while (!bSocket.isConnected && counter < 3)

        return bSocket.isConnected
    }

    fun sendMessageToText(message: ByteArray) {
        try {
            val oStream: OutputStream? = bSocket.outputStream
            oStream?.write(message ,0, message.size)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun sendJoystickCommand(message: ByteArray) {
        try {
            val oStream: OutputStream? = bSocket.outputStream
            oStream?.write(message ,0, message.size)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun close(): Boolean {
        return try {
            bSocket.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}