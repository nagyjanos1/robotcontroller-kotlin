package com.example.robotcontroller.editCommand

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.example.robotcontroller.R
import com.example.robotcontroller.adapter.BluetoothDeviceDataHolder
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.FbdlCommandItem
import com.example.robotcontroller.data.Universe
import com.example.robotcontroller.fragments.universe.UniverseFragment

class EditCommandActivity : AppCompatActivity() {

    private lateinit var commandName: EditText
    private lateinit var universeSpinner: Spinner
    private lateinit var setAsDefaultChk: CheckBox

    private lateinit var btnHandleUniverse: Button
    private lateinit var btnHandleRules: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_command)

        var currentFbdlCommandId: Long? = null

        commandName = findViewById(R.id.edit_command_name)
        universeSpinner = findViewById(R.id.select_rulebase_universe)
        setAsDefaultChk = findViewById(R.id.edit_command_current)

        btnHandleUniverse = findViewById(R.id.handle_universe)
        btnHandleRules = findViewById(R.id.handle_rules)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFbdlCommandId = bundle.getLong("itemId")
        }

        /* If currentFlowerId is not null, get corresponding flower and set name, image and
        description */
        currentFbdlCommandId?.let {
            btnHandleUniverse.setOnClickListener {
                val intent = Intent(this, UniverseFragment()::class.java)
                intent.putExtra("itemId", currentFbdlCommandId)
                startActivityFromFragment( intent)
            }

            val database: AppDatabase = AppDatabase.getInstance(this)
            val currentFbdlCommand: FbdlCommandItem? = database
                .fbdlCommandItemDao()
                .findItemById(currentFbdlCommandId)

            var mutableUniverseList: ArrayList<Universe> = ArrayList()

            var universeList = database
                .universeDao()
                .findAllItems()
                .value
            if (universeList != null) {
                for (universe in universeList) {
                    mutableUniverseList.add(universe)
                }
            }

            val universeListAdapter = ArrayAdapter(this, android.R.layout.activity_list_item, mutableUniverseList)
            commandName.setText(currentFbdlCommand?.name.orEmpty())
            universeSpinner.adapter = universeListAdapter
            setAsDefaultChk.isChecked = currentFbdlCommand?.isDefault ?: false

            findViewById<Button>(R.id.edit_btnDelete).setOnClickListener {
                currentFbdlCommand?.let { delItem ->
                    AppDatabase.getInstance(this).fbdlCommandItemDao().deleteItem(
                        delItem
                    )
                }
                finish()
            }

            val btnEdit = findViewById<Button>(R.id.edit_btnDone)
            btnEdit.setOnClickListener {
                if (commandName.text.isNullOrEmpty()) {
                    // validation
                } else {
                    currentFbdlCommand?.let { editItem ->
                        editItem.name = commandName.text.toString()
                        //editItem.uni = commandText.text.toString()
                        editItem.isDefault = setAsDefaultChk.isChecked

                        AppDatabase.getInstance(this).fbdlCommandItemDao().updateItem(
                            editItem
                        )
                    }
                }
                finish()
            }
        }
    }

    private fun editFbdlCommand() {
        val resultIntent = Intent()

        if (commandName.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = commandName.text.toString()
            //val description = commandText.text.toString()
            resultIntent.putExtra("name", name)
            //resultIntent.putExtra("fbdl command", description)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}