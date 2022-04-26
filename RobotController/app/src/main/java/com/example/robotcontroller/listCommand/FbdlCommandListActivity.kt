package com.example.robotcontroller.listCommand

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.robotcontroller.MainActivity
import com.example.robotcontroller.R
import com.example.robotcontroller.adapter.CommandsAdapter
import com.example.robotcontroller.addCommand.AddCommandActivity
import com.example.robotcontroller.data.FbdlCommandItem
import com.example.robotcontroller.editCommand.EditCommandActivity
import com.example.robotcontroller.joystick.JoystickActivity
import com.example.robotcontroller.viewmodels.FbdlCommandItemViewModel
import com.example.robotcontroller.viewmodels.FbdlCommandItemViewModelFactory

class FbdlCommandListActivity : AppCompatActivity() {
    private val newFbdlCommandActivityRequestCode = 1
    private lateinit var commandsAdapter: CommandsAdapter
    private lateinit var currentDeviceAddress: String

    private val fbdlListViewModel by viewModels<FbdlCommandItemViewModel> {
        FbdlCommandItemViewModelFactory(this@FbdlCommandListActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fbdl_command_list)

        currentDeviceAddress = intent.getStringExtra(MainActivity.EXTRA_ADDRESS).toString()
        if (currentDeviceAddress.isNullOrEmpty()) {
            Toast.makeText(this, "Address is not available.", Toast.LENGTH_LONG).show()
            return
        }

        commandsAdapter = CommandsAdapter {
            fbdlCommandItem -> adapterOnClick(fbdlCommandItem)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerCommands)
        recyclerView.adapter = commandsAdapter

        fbdlListViewModel.getAll().observe(this){ items ->
            commandsAdapter.submitList(items)
        }

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddCommandActivity::class.java)
            startActivityForResult(intent, newFbdlCommandActivityRequestCode)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.fbdllist_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.btnJoystick -> {
                val intent = Intent(this, JoystickActivity()::class.java)
                intent.putExtra(MainActivity.EXTRA_ADDRESS, currentDeviceAddress)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun adapterOnClick(fbdlCommand: FbdlCommandItem) {
        val intent = Intent(this, EditCommandActivity()::class.java)
        intent.putExtra("itemId", fbdlCommand.itemId)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newFbdlCommandActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val fbdlCommandName = data.getStringExtra("name")
                val fbdlCommandText = data.getStringExtra("fbdl command")

                fbdlListViewModel.insert(fbdlCommandName ?: "", fbdlCommandText ?: "")
            }
        }
    }
}