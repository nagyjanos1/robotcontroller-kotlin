package com.example.robotcontroller.editCommand

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
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

    private lateinit var commandNameEditText: EditText
    private lateinit var commandDescriptionEditText: EditText
    private lateinit var setAsDefaultChk: CheckBox

    private lateinit var btnHandleUniverse: Button
    private lateinit var btnHandleUniverseParameters: Button
    private lateinit var btnHandleRules: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_command)

        var currentFbdlCommandId: Long? = null

        commandNameEditText = findViewById(R.id.edit_command_name)
        commandDescriptionEditText = findViewById(R.id.edit_command_description)
        setAsDefaultChk = findViewById(R.id.edit_command_current)

        btnHandleUniverse = findViewById(R.id.handle_universe)
        btnHandleUniverseParameters = findViewById(R.id.handle_universe_parameters)
        btnHandleRules = findViewById(R.id.handle_rules)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFbdlCommandId = bundle.getLong("itemId")
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

            val btnEdit = findViewById<Button>(R.id.edit_btnDone)
            btnEdit.setOnClickListener {
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
    }

    private fun editFbdlCommand() {
        val resultIntent = Intent()

        if (commandNameEditText.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = commandNameEditText.text.toString()
            val description = commandDescriptionEditText.text.toString()
            resultIntent.putExtra("name", name)
            resultIntent.putExtra("description", description)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}