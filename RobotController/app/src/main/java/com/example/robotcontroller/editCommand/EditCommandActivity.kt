package com.example.robotcontroller.editCommand

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.robotcontroller.R
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.FbdlCommandItem

class EditCommandActivity : AppCompatActivity() {

    private lateinit var commandName: EditText
    private lateinit var commandText: EditText
    private lateinit var setAsDefaultChk: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_command)

        /*findViewById<Button>(R.id.add_btnDone).setOnClickListener {
            editFbdlCommand()
        }*/

        var currentFbdlCommandId: Long? = null

        commandName = findViewById(R.id.edit_command_name)
        commandText = findViewById(R.id.edit_command_fbdl)
        setAsDefaultChk = findViewById(R.id.edit_command_current)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFbdlCommandId = bundle.getLong("itemId")
        }

        /* If currentFlowerId is not null, get corresponding flower and set name, image and
        description */
        currentFbdlCommandId?.let {
            val currentFbdlCommand: FbdlCommandItem? = AppDatabase.getInstance(this)
                .fbdlCommandItemDao()
                .findItemById(currentFbdlCommandId)

            commandName.setText(currentFbdlCommand?.name.orEmpty())
            commandText.setText(currentFbdlCommand?.fbdl.orEmpty())
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
                if (commandName.text.isNullOrEmpty() || commandText.text.isNullOrEmpty()) {
                    // validation
                } else {
                    currentFbdlCommand?.let { editItem ->
                        editItem.name = commandName.text.toString()
                        editItem.fbdl = commandText.text.toString()
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

        if (commandName.text.isNullOrEmpty() || commandText.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = commandName.text.toString()
            val description = commandText.text.toString()
            resultIntent.putExtra("name", name)
            resultIntent.putExtra("fbdl command", description)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}