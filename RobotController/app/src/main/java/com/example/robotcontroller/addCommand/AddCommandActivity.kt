package com.example.robotcontroller.addCommand

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.robotcontroller.R

class AddCommandActivity : AppCompatActivity() {

    private lateinit var commandName: EditText
    private lateinit var commandText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_command)

        findViewById<Button>(R.id.add_btnDone).setOnClickListener {
            addFbdlCommand()
        }

        commandName = findViewById(R.id.add_command_name)
        commandText = findViewById(R.id.add_command_fbdl)
    }

    private fun addFbdlCommand() {
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