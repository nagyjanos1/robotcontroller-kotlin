package com.example.robotcontroller.joystick

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.robotcontroller.MainActivity
import com.example.robotcontroller.R
import com.example.robotcontroller.joystick.CommonBluetoothHandler.Companion.MESSAGE_DEVICE_NAME
import com.example.robotcontroller.joystick.CommonBluetoothHandler.Companion.MESSAGE_READ
import com.example.robotcontroller.joystick.CommonBluetoothHandler.Companion.MESSAGE_STATE_CHANGE
import com.example.robotcontroller.joystick.CommonBluetoothHandler.Companion.MESSAGE_TOAST
import com.example.robotcontroller.joystick.CommonBluetoothHandler.Companion.MESSAGE_WRITE
import com.example.robotcontroller.joystick.CommonBluetoothHandler.Companion.TOAST
import java.util.*

class JoystickActivity: AppCompatActivity() {
    private lateinit var mUUID: UUID

    /**
     * String buffer for outgoing messages
     */
    private var mOutStringBuffer: StringBuffer? = null

    /**
     * Local Bluetooth adapter
     */
    private var mBluetoothAdapter: BluetoothAdapter? = null

    /**
     * Member object for the chat services
     */
    private var mBluetoothHandler: CommonBluetoothHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)

        mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = StringBuffer()

        findViewById<Button>(R.id.send_a_message).setOnClickListener {
            sendMessage("hello")
        }

        findViewById<Button>(R.id.receive_a_message).setOnClickListener {
            //sendMessage("hello")

        }
    }

    override fun onStart() {
        super.onStart()
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            return
        }

        if (mBluetoothHandler == null) {
            //initBluetooth()

            mBluetoothHandler = CommonBluetoothHandler(this, mHandler)
            connectDevice(this.intent)
        }
    }

    override fun onResume() {
        super.onResume()

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

    override fun onDestroy() {
        mBluetoothHandler?.stop()

        super.onDestroy()
    }

    private fun connectDevice(data: Intent) {
        // Get the device MAC address
        val address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)
        if (address.isNullOrEmpty()) {
            Toast.makeText(this, "Address is not available.", Toast.LENGTH_LONG).show()
            return
        }

        // Get the BluetoothDevice object
        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        // Attempt to connect to the device
        mBluetoothHandler?.connect(device)
    }

    private fun sendMessage(message: String) {
        // Check that we're actually connected before trying anything
        if (mBluetoothHandler?.getState() !== CommonBluetoothHandler.STATE_CONNECTED) {
            Toast.makeText(this, "NOT CONNECTED", Toast.LENGTH_SHORT).show()
            return
        }

        // Check that there's actually something to send
        if (message.isNotEmpty()) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            mBluetoothHandler?.write(send)

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer!!.setLength(0)
            findViewById<TextView>(R.id.mOutEditText).text = mOutStringBuffer
        }
    }

    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                   CommonBluetoothHandler.STATE_CONNECTED ->
                       Toast.makeText(this@JoystickActivity, "CONNECTED", Toast.LENGTH_SHORT).show()
                    CommonBluetoothHandler.STATE_CONNECTING ->
                        Toast.makeText(this@JoystickActivity, "CONNECTING", Toast.LENGTH_SHORT).show()
                    CommonBluetoothHandler.STATE_LISTEN, CommonBluetoothHandler.STATE_NONE ->
                        Toast.makeText(this@JoystickActivity, "LISTEN", Toast.LENGTH_SHORT).show()
                }
                MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    Toast.makeText(this@JoystickActivity, writeMessage, Toast.LENGTH_LONG).show()
                }
                MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    Toast.makeText(this@JoystickActivity,
                        readMessage,
                        Toast.LENGTH_SHORT)
                        .show()
                }
                MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    val mConnectedDeviceName = msg.data.getString(CommonBluetoothHandler.DEVICE_NAME)
                    Toast.makeText(this@JoystickActivity,
                        "Connected to $mConnectedDeviceName",
                        Toast.LENGTH_SHORT)
                    .show()
                }
                MESSAGE_TOAST ->
                    Toast.makeText(this@JoystickActivity,
                        msg.data.getString(TOAST),
                        Toast.LENGTH_SHORT)
                    .show()

            }
        }
    }
}