package com.example.robotcontroller.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.robotcontroller.R
import com.example.robotcontroller.data.Universe

class PairedBluetoothDeviceListAdapter(
    private val context: Activity,
    private val deviceData: ArrayList<BluetoothDeviceDataHolder>)
    : ArrayAdapter<BluetoothDeviceDataHolder>(context, R.layout.device_listitem_layout, deviceData) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.device_listitem_layout, null, true)

        val deviceListItemNameText = rowView.findViewById(R.id.deviceListItemName) as TextView
        val deviceListItemAddressText = rowView.findViewById(R.id.deviceListItemAddress) as TextView

        deviceListItemNameText.text = deviceData[position].name
        deviceListItemAddressText.text = deviceData[position].address

        return rowView
    }
}

/*
class UniverseListAdapter(
    private val context: Activity,
    private val deviceData: ArrayList<Universe>)
    : ArrayAdapter<Universe>(context, R.layout.fragment_universe, deviceData) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.fragment_universe, null, true)

        val deviceListItemNameText = rowView.findViewById(R.id.deviceListItemName) as TextView
        val deviceListItemAddressText = rowView.findViewById(R.id.deviceListItemAddress) as TextView

        deviceListItemNameText.text = deviceData[position].name
        deviceListItemAddressText.text = deviceData[position].address

        return rowView
    }
}*/