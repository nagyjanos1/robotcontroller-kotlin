package com.example.robotcontroller.fbdl

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.robotcontroller.R

class AddCommandActivity : AppCompatActivity() {

    companion object {
        val FBDL_NAME : String = "NAME"
        val FBDL_DESCRIPTION : String = "DESCRIPTION"
    }

    private lateinit var commandName: EditText
    private lateinit var commandDescriptionText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_command)

        findViewById<Button>(R.id.add_btnDone).setOnClickListener {
            addFbdlCommand()
        }

        commandName = findViewById(R.id.add_command_name)
        commandDescriptionText = findViewById(R.id.add_command_description)
    }

    private fun addFbdlCommand() {
        val resultIntent = Intent()

        if (commandName.text.isNullOrEmpty() || commandDescriptionText.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = commandName.text.toString()
            val description = commandDescriptionText.text.toString()
            resultIntent.putExtra(FBDL_NAME, name)
            resultIntent.putExtra(FBDL_DESCRIPTION, description)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}