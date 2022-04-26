package com.example.robotcontroller.joystick

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.robotcontroller.MainActivity
import com.example.robotcontroller.MainActivity.Companion.REQUEST_ENABLE_BLUETOOTH
import com.example.robotcontroller.R
import java.io.IOException
import java.io.OutputStream
import java.util.*

class JoystickActivity : AppCompatActivity() {
    private lateinit var mUUID: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joystick)

        mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        displayJoystick()

        connectToDevice()
    }

    private fun connectToDevice() {
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
            Toast.makeText(this, "Bluetooth socket is created.", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var counter = 0
        do
            try {
                counter++
                bSocket?.connect()
                Toast.makeText(this, "Bluetooth socket is connected.", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        while (bSocket?.isConnected == false && counter < 3)

        var message = "Hello".toByteArray()
        try {
            val oStream: OutputStream? = bSocket?.outputStream
            oStream?.let {
                it.write(message ,0, message.size)
                //it.write("Hello".toByteArray())
                Toast.makeText(this, "A byte was sent.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        message = ByteArray(1) { 0 }
        try {
            val oStream: OutputStream? = bSocket?.outputStream
            oStream?.let {
                it.write(message ,0,1)
                //it.write("Hello".toByteArray())
                Toast.makeText(this, "A byte was sent.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            bSocket?.close()
            Toast.makeText(this, "Bluetooth socket connection is closed.", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun displayJoystick() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val bitmap: Bitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // canvas background color
        canvas.drawARGB(255, 78, 168, 186);

        val paint = Paint()
        paint.color = Color.parseColor("#FFFFFF")
        paint.strokeWidth = 30F
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.isDither = true


        // circle center
        val center_x = (displayMetrics.widthPixels/2).toFloat()
        val center_y = (displayMetrics.heightPixels/2).toFloat()
        val radius = 300F

        canvas.drawCircle(center_x, center_y, radius, paint)

        // set bitmap as background to ImageView
        val imageV = this.findViewById<ImageView>(R.id.imageV)
        imageV.background = BitmapDrawable(resources, bitmap)
    }
}