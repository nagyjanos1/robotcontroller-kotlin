package com.example.robotcontroller.fbdl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.robotcontroller.R
import com.example.robotcontroller.bluetooth.BluetoothHandlerService
import com.example.robotcontroller.bluetooth.MicroFRIHandler
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.FbdlCommandItem
import com.example.robotcontroller.limit.LimitListActivity
import com.example.robotcontroller.rule.RuleListActivity
import com.example.robotcontroller.universe.UniverseListActivity

class EditCommandActivity : AppCompatActivity() {

    companion object {
        val FBDL_ID : String = "FBDL_ID"
    }

    private val editUniverseActivityRequestCode = 1
    private val editLimitActivityRequestCode = 1
    private val editRuleActivityRequestCode = 1

    private lateinit var commandNameEditText: EditText
    private lateinit var commandDescriptionEditText: EditText
    private lateinit var setAsDefaultChk: CheckBox

    private lateinit var btnHandleUniverse: Button
    private lateinit var btnHandleUniverseParameters: Button
    private lateinit var btnHandleRules: Button

    private lateinit var mFriHandler: MicroFRIHandler
    private lateinit var mBluetoothHandlerService: BluetoothHandlerService

    /** Lifecycle hooks */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_command)

        mFriHandler = MicroFRIHandler()
        mBluetoothHandlerService = BluetoothHandlerService(this, mFriHandler)
        mBluetoothHandlerService.setBluetoothConnection()

        var currentFbdlCommandId: Long? = null

        commandNameEditText = findViewById(R.id.edit_command_name)
        commandDescriptionEditText = findViewById(R.id.edit_command_description)
        setAsDefaultChk = findViewById(R.id.edit_command_current)

        btnHandleUniverse = findViewById(R.id.handle_universe)
        btnHandleUniverseParameters = findViewById(R.id.handle_universe_parameters)
        btnHandleRules = findViewById(R.id.handle_rules)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFbdlCommandId = bundle.getLong(FBDL_ID)
        }

        currentFbdlCommandId?.let {
            btnHandleUniverse.setOnClickListener {
                val intent = Intent(this, UniverseListActivity()::class.java)
                intent.putExtra("itemId", currentFbdlCommandId)
                startActivityForResult( intent, editUniverseActivityRequestCode)
            }

            btnHandleUniverseParameters.setOnClickListener {
                val intent = Intent(this, LimitListActivity()::class.java)
                intent.putExtra("itemId", currentFbdlCommandId)
                startActivityForResult( intent, editLimitActivityRequestCode)
            }

            btnHandleRules.setOnClickListener {
                val intent = Intent(this, RuleListActivity()::class.java)
                intent.putExtra("itemId", currentFbdlCommandId)
                startActivityForResult( intent, editRuleActivityRequestCode)
            }

            val database: AppDatabase = AppDatabase.getInstance(this)
            val currentFbdlCommand: FbdlCommandItem? = database
                .fbdlCommandItemDao()
                .findItemById(currentFbdlCommandId)

            commandNameEditText.setText(currentFbdlCommand?.name.orEmpty())
            commandDescriptionEditText.setText(currentFbdlCommand?.description.orEmpty())
            setAsDefaultChk.isChecked = currentFbdlCommand?.isDefault ?: false

            findViewById<Button>(R.id.edit_btnDelete).setOnClickListener {
                currentFbdlCommand?.let { delItem ->
                    AppDatabase.getInstance(this).fbdlCommandItemDao().deleteItem(
                        delItem
                    )
                }
                finish()
            }

            val btnDone = findViewById<Button>(R.id.edit_btnDone)
            btnDone.setOnClickListener {
                if (commandNameEditText.text.isNullOrEmpty()) {
                    // validation
                } else {
                    currentFbdlCommand?.let { editItem ->
                        editItem.name = commandNameEditText.text.toString()
                        editItem.description = commandDescriptionEditText.text.toString()
                        editItem.isDefault = setAsDefaultChk.isChecked

                        AppDatabase.getInstance(this).fbdlCommandItemDao().updateItem(
                            editItem
                        )
                    }
                }
                finish()
            }
        }

        val btnSendAll = findViewById<Button>(R.id.sendAll)
        btnSendAll.setOnClickListener {
            val database = AppDatabase.getInstance(this)
            val universes = database.universeDao().findAllByFbdlId(currentFbdlCommandId!!)
            val limits = database.limitsDao().getAllByFbdlId(currentFbdlCommandId)
            val rules = database.ruleDao().getAllByFbdlId(currentFbdlCommandId)

            val initFrame = mFriHandler.createInitFrame(universes.size, rules?.size ?: 0)
            mBluetoothHandlerService.sendFrame(initFrame)

            val universeFrame = mFriHandler.createUniverses(ArrayList(universes), ArrayList(limits!!))
            mBluetoothHandlerService.sendFrame(universeFrame)

            val ruleFrame = mFriHandler.createRule(ArrayList(rules!!))
            mBluetoothHandlerService.sendFrame(ruleFrame)
        }
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