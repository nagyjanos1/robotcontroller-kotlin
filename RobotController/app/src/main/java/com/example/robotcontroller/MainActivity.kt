package com.example.robotcontroller

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.robotcontroller.R.layout.activity_main
import com.example.robotcontroller.adapter.BluetoothDeviceDataHolder
import com.example.robotcontroller.adapter.PairedBluetoothDeviceListAdapter
import com.example.robotcontroller.bluetooth.BluetoothBroadcastReceiver
import com.example.robotcontroller.joystick.JoystickActivity
import com.example.robotcontroller.fbdl.FbdlCommandListActivity

class MainActivity() : AppCompatActivity() {

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
        val REQUEST_ENABLE_BLUETOOTH: Int = 1
    }

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var mBroadcastReceiver: BluetoothBroadcastReceiver

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        mBroadcastReceiver = BluetoothBroadcastReceiver(this)

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mBroadcastReceiver, filter)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isEnabled) {
            val bluetoothStatus: ImageView = findViewById(R.id.imageBluetoothStatus)
            val bluetoothStatusText: TextView = this.findViewById(R.id.bluetoothStatus)

            bluetoothStatusText.text = this.getString(R.string.bluetooth_is_turning_on)
            bluetoothStatus.setImageResource(R.drawable.ic_bluetooth_on)

            displayPairedDevices()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.btnRefreshPairedDevice -> {
                displayPairedDevices()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothAdapter!!.isEnabled) {
                    Toast.makeText(this, "Bluetooth has been enabled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Bluetooth has been disabled", Toast.LENGTH_LONG).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth enabling has been canceled", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

    fun setBluetoothStatusOnDisplay(status: Int) {
        val bluetoothStatus: ImageView = findViewById(R.id.imageBluetoothStatus)
        val bluetoothStatusText: TextView = this.findViewById(R.id.bluetoothStatus)

        when (status) {
            BluetoothAdapter.STATE_OFF -> {
                bluetoothStatusText.text = this.getString(R.string.bluetooth_is_not_available)
                bluetoothStatus.setImageResource(R.drawable.ic_bluetooth_disabled)
            }
            BluetoothAdapter.STATE_TURNING_OFF -> {
                bluetoothStatusText.text = this.getString(R.string.bluetooth_is_turning_off)
                bluetoothStatus.setImageResource(R.drawable.ic_bluetooth_disabled)
            }
            BluetoothAdapter.STATE_ON -> {
                bluetoothStatusText.text = this.getString(R.string.bluetooth_is_available)
                bluetoothStatus.setImageResource(R.drawable.ic_bluetooth_on)
            }
            BluetoothAdapter.STATE_TURNING_ON -> {
                bluetoothStatusText.text = this.getString(R.string.bluetooth_is_turning_on)
                bluetoothStatus.setImageResource(R.drawable.ic_bluetooth_on)
            }
        }
    }

    private fun joystickOnClick(dataHolder: BluetoothDeviceDataHolder) {
        val intent = Intent(this, JoystickActivity()::class.java)
        intent.putExtra(EXTRA_ADDRESS, dataHolder.address)
        ContextCompat.startActivity(this, intent, null)
    }

    private fun fbdlCommandListActivity(dataHolder: BluetoothDeviceDataHolder) {
        val intent = Intent(this, FbdlCommandListActivity()::class.java)
        intent.putExtra(EXTRA_ADDRESS, dataHolder.address)
        ContextCompat.startActivity(this, intent, null)
    }

    private fun displayPairedDevices() {
        val checkSelfPermissionResult = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.BLUETOOTH_CONNECT)
        if (checkSelfPermissionResult != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
            }
            return
        }

        val pairedDevices = bluetoothAdapter.bondedDevices
        val deviceDataList: ArrayList<BluetoothDeviceDataHolder> = ArrayList()

        if (pairedDevices.isEmpty()) {
            Toast.makeText(
                this, "No paired bluetooth devices found", Toast.LENGTH_LONG
            ).show()
            return
        }

        for (device in pairedDevices) {
            deviceDataList.add(BluetoothDeviceDataHolder(device.name, device.address))
        }

        val deviceListView = this.findViewById<ListView>(R.id.pairedDeviceList)
        val deviceListAdapter = PairedBluetoothDeviceListAdapter(this, deviceDataList)

        deviceListView.adapter = deviceListAdapter
        deviceListView.onItemClickListener = AdapterView.OnItemClickListener {
            _, _, position,_ ->
            run {
                val deviceData: BluetoothDeviceDataHolder = deviceDataList[position]

                val intent = Intent(this, FbdlCommandListActivity::class.java)
                intent.putExtra(EXTRA_ADDRESS, deviceData.address)
                startActivity(intent)
            }
        }
    }
}