package com.example.robotcontroller.universe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.robotcontroller.R
import com.example.robotcontroller.adapter.UniverseAdapter
import com.example.robotcontroller.data.entities.Universe
import com.example.robotcontroller.universe.fragments.EditUniverseFragment
import com.example.robotcontroller.viewmodels.GenericViewModelFactory
import com.example.robotcontroller.viewmodels.UniverseViewModel

interface OnInputListener {
    fun sendInput(input: String?)
    fun setToNull()
}

class UniverseListActivity : AppCompatActivity(), OnInputListener {

    private val newUniverseActivityRequestCode = 1
    private lateinit var universeAdapter: UniverseAdapter

    private var latestUniverse : Universe? = null
    private var currentFbdlCommandId: Long? = null

    private val universeViewModel by viewModels<UniverseViewModel> {
        GenericViewModelFactory(this@UniverseListActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_universe_list)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFbdlCommandId = bundle.getLong("itemId")
        }

        universeAdapter = UniverseAdapter {
            universeItem -> adapterOnClick(universeItem)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerUniverse)
        recyclerView.adapter = universeAdapter

        universeViewModel.getAll().observe(this){ items ->
            universeAdapter.submitList(items)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.general_crud_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.btnAdd -> {
                // hozzáadás
                latestUniverse = Universe(null, "", currentFbdlCommandId!!)

                val newFragment = EditUniverseFragment.newInstance("")
                newFragment.show(supportFragmentManager, "Set universe name")
                true
            }
            R.id.btnDelete -> {
                // törlés
                val removableUniverse = universeAdapter.getRemovableUniverse()
                removableUniverse.forEach {
                    universeViewModel.remove(it)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun sendInput(input: String?) {
        latestUniverse?.name = input!!

        if (latestUniverse?.id == null) {
            universeViewModel.insert(input ?: "", currentFbdlCommandId)
        } else {
            universeViewModel.update(latestUniverse!!)
        }
    }

    override fun setToNull() {
        latestUniverse = null
    }

    private fun adapterOnClick(universe: Universe) {
        latestUniverse = universe

        val universeName = latestUniverse?.name!!
        val newFragment = EditUniverseFragment.newInstance(universeName)
        newFragment.show(supportFragmentManager, "Set universe name")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newUniverseActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                // Dialógusból visszajövet adatokat csenünk
            }
        }
    }
}