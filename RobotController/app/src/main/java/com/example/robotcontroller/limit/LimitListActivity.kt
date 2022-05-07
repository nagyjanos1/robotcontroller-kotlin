package com.example.robotcontroller.limit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.robotcontroller.R
import com.example.robotcontroller.adapter.LimitAdapter
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.Limit
import com.example.robotcontroller.limit.fragments.EditLimitFragment
import com.example.robotcontroller.viewmodels.GenericViewModelFactory
import com.example.robotcontroller.viewmodels.LimitViewModel

interface OnInputListenerForLimit {
    fun sendInput(minValue: Int, maxValue: Int, universeId: Long)
    fun setToNull()
}

class LimitListActivity : AppCompatActivity(), OnInputListenerForLimit {

    private val newLimitActivityRequestCode = 1
    private lateinit var limitAdapter: LimitAdapter

    private var latestLimit : Limit? = null
    private var currentFbdlCommandId: Long? = null

    private val limitViewModel by viewModels<LimitViewModel> {
        GenericViewModelFactory(this@LimitListActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_limit_list)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFbdlCommandId = bundle.getLong("itemId")
        }

        limitAdapter = LimitAdapter {
                limitItem -> adapterOnClick(limitItem)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerLimit)
        recyclerView.adapter = limitAdapter

        limitViewModel.getAll().observe(this){ items ->
            limitAdapter.submitList(items)
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
                latestLimit = Limit(null, "", 0, 0, null)

                val newFragment = EditLimitFragment.newInstance(0, 0, 0, currentFbdlCommandId)
                newFragment.show(supportFragmentManager, "Set limit name")
                true
            }
            R.id.btnDelete -> {
                // törlés
                val removableLimit = limitAdapter.getRemovableLimits()
                removableLimit.forEach {
                    limitViewModel.remove(it)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun sendInput(minValue: Int, maxValue: Int, universeId: Long) {
        latestLimit?.minValue = minValue
        latestLimit?.maxValue = maxValue
        latestLimit?.universeId = universeId

        if (latestLimit?.id == null) {
            limitViewModel.insert("", minValue, maxValue, universeId)
        } else {
            limitViewModel.update(latestLimit!!)
        }
    }

    override fun setToNull() {
        latestLimit = null
    }

    private fun adapterOnClick(limit: Limit) {
        latestLimit = limit

        val universe = latestLimit!!.universeId?.let {
            AppDatabase.getInstance(this).universeDao().findItemById(it)
        }

        val newFragment = EditLimitFragment.newInstance(
            latestLimit!!.minValue,
            latestLimit!!.maxValue,
            universe!!.id,
            universe.fbdlCommandItemId)
        newFragment.show(supportFragmentManager, "Set limit name")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newLimitActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                // Dialógusból visszajövet adatokat csenünk
            }
        }
    }


}