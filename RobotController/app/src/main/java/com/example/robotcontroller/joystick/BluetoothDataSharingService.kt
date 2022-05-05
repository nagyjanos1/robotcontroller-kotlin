package com.example.robotcontroller.joystick

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.OutputStream

class BluetoothDataSharingService(private val bSocket: BluetoothSocket) {

    var messages: ArrayList<String> = ArrayList()

    @SuppressLint("MissingPermission")
    fun connect(): Boolean {
        var counter = 0
        do
            try {
                counter++

                bSocket.connect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        while (!bSocket.isConnected && counter < 3)

        return bSocket.isConnected
    }

    fun sendMessageToText(message: ByteArray): Boolean {
        return try {
            val oStream: OutputStream? = bSocket.outputStream
            oStream?.write(message ,0, message.size)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun readBlueToothData() {
        val buffer = ByteArray(1024)
        var bytes: Int
        while (true) {
            try {
                bytes = bSocket.inputStream.read(buffer)
                val readMessage = String(buffer, 0, bytes)
                messages.add(readMessage)
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
    }

    fun sendJoystickCommand(message: ByteArray): Boolean {
        return try {
            val oStream: OutputStream? = bSocket.outputStream
            oStream?.write(message ,0, message.size)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
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

