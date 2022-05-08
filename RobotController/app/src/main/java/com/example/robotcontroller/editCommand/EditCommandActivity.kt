package com.example.robotcontroller.editCommand

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.robotcontroller.R
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.FbdlCommandItem
import com.example.robotcontroller.limit.LimitListActivity
import com.example.robotcontroller.rule.RuleListActivity
import com.example.robotcontroller.universe.UniverseListActivity

class EditCommandActivity : AppCompatActivity() {

    private val editUniverseActivityRequestCode = 1
    private val editLimitActivityRequestCode = 1
    private val editRuleActivityRequestCode = 1

    private lateinit var commandName: EditText
    private lateinit var setAsDefaultChk: CheckBox

    private lateinit var btnHandleUniverse: Button
    private lateinit var btnHandleUniverseParameters: Button
    private lateinit var btnHandleRules: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_command)

        var currentFbdlCommandId: Long? = null

        commandName = findViewById(R.id.edit_command_name)
        setAsDefaultChk = findViewById(R.id.edit_command_current)

        btnHandleUniverse = findViewById(R.id.handle_universe)
        btnHandleUniverseParameters = findViewById(R.id.handle_universe_parameters)
        btnHandleRules = findViewById(R.id.handle_rules)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFbdlCommandId = bundle.getLong("itemId")
        }

        /* If currentFlowerId is not null, get corresponding flower and set name, image and
        description */
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

            commandName.setText(currentFbdlCommand?.name.orEmpty())
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