package com.example.robotcontroller.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.example.robotcontroller.MainActivity
import java.util.*

class BluetoothHandlerService(
    private val context: Context,
    private val mFriHandler: MicroFRIHandler
) {

    private lateinit var mUUID: UUID

    private var mOutStringBuffer: StringBuffer? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothHandler: CommonBluetoothHandler? = null

    fun setBluetoothConnection() {
        mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mOutStringBuffer = StringBuffer()
    }

    fun createBluetoothHandler(intent: Intent) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            return
        }

        if (mBluetoothHandler == null) {
            mBluetoothHandler = CommonBluetoothHandler(mHandler)
            connectDevice(intent)
        }
    }

    fun connectDevice(intent: Intent) {
        val address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)
        if (address.isNullOrEmpty()) {
            Toast.makeText(context, "Address is not available.", Toast.LENGTH_LONG).show()
            return
        }

        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        mBluetoothHandler?.connect(device)
    }

    fun startBluetooth() {
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothHandler != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothHandler!!.getState() === CommonBluetoothHandler.STATE_NONE) {
                // Start the Bluetooth chat services
                mBluetoothHandler!!.start()
            }
        }
    }

    fun stopBluetooth() {
        mBluetoothHandler?.stop()
    }

    fun sendFrame(msg: ByteArray) {
        if (mBluetoothHandler?.getState() !== CommonBluetoothHandler.STATE_CONNECTED) {
            Toast.makeText(context, "NOT CONNECTED", Toast.LENGTH_SHORT).show()
            return
        }

        if (msg.isNotEmpty()) {
            mBluetoothHandler?.write(msg)
            mOutStringBuffer!!.setLength(0)
        }
    }
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CommonBluetoothHandler.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    CommonBluetoothHandler.STATE_CONNECTED ->
                        Toast.makeText(context, "CONNECTED", Toast.LENGTH_SHORT).show()
                    CommonBluetoothHandler.STATE_CONNECTING ->
                        Toast.makeText(context, "CONNECTING", Toast.LENGTH_SHORT).show()
                    CommonBluetoothHandler.STATE_LISTEN, CommonBluetoothHandler.STATE_NONE ->
                        Toast.makeText(context, "LISTEN", Toast.LENGTH_SHORT).show()
                }
                CommonBluetoothHandler.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    val writeMessage = String(writeBuf)
                    Toast.makeText(context, writeMessage, Toast.LENGTH_LONG).show()
                }
                CommonBluetoothHandler.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    val readMessage = mFriHandler.getResponse(readBuf)
                    Toast.makeText(
                        context,
                        readMessage,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                CommonBluetoothHandler.MESSAGE_DEVICE_NAME -> {
                    val mConnectedDeviceName = msg.data.getString(CommonBluetoothHandler.DEVICE_NAME)
                    Toast.makeText(
                        context,
                        "Connected to $mConnectedDeviceName",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                CommonBluetoothHandler.MESSAGE_TOAST ->
                    Toast.makeText(
                        context,
                        msg.data.getString(CommonBluetoothHandler.TOAST),
                        Toast.LENGTH_SHORT
                    )
                        .show()

            }
        }
    }

}