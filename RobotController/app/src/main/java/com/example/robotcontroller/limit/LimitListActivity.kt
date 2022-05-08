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
import com.example.robotcontroller.adapter.LimitModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.Limit
import com.example.robotcontroller.limit.fragments.EditLimitFragment
import com.example.robotcontroller.viewmodels.GenericViewModelFactory
import com.example.robotcontroller.viewmodels.LimitViewModel

interface OnInputListenerForLimit {
    fun sendInput(name: String, minValue: Int, maxValue: Int, universeId: Long)
    fun setToNull()
}

class LimitListActivity : AppCompatActivity(), OnInputListenerForLimit {

    private val newLimitActivityRequestCode = 1
    private lateinit var limitAdapter: LimitAdapter

    private var latestLimitModel : LimitModel? = null
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
            val limitModels = items.map {
                val universe = AppDatabase.getInstance(this).universeDao().findItemById(it.universeId!!)

                LimitModel(
                    it.id,
                    it.name,
                    it.minValue ?: 0,
                    it.maxValue ?: 0,
                    it.universeId!!,
                    universe?.name!!,
                    universe?.fbdlCommandItemId!!)
            }

            limitAdapter.submitList(limitModels)
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
                latestLimitModel = LimitModel(
                    null,
                    "",
                    0,
                    0,
                    null,
                    "",
                    currentFbdlCommandId)

                val newFragment = EditLimitFragment.newInstance(latestLimitModel!!)
                newFragment.show(supportFragmentManager, "Set limit name")
                true
            }
            R.id.btnDelete -> {
                // törlés
                val removableLimitModels = limitAdapter.getRemovableLimits()
                val removableLimitIds = removableLimitModels.map {
                    it.id!!
                }
                val removableLimit = limitViewModel.findItemByIds(removableLimitIds)
                removableLimit?.forEach {
                    limitViewModel.remove(it)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun sendInput(name: String, minValue: Int, maxValue: Int, universeId: Long) {
        if (latestLimitModel?.id == null) {
            limitViewModel.insert(name, minValue, maxValue, universeId)
        } else {
            val limit = Limit(
                latestLimitModel?.id,
                name,
                minValue,
                maxValue,
                universeId)
            limitViewModel.update(limit!!)
        }
    }

    override fun setToNull() {
        latestLimitModel = null
    }

    private fun adapterOnClick(limit: LimitModel) {
        latestLimitModel = limit

        val universe = latestLimitModel!!.universeId?.let {
            AppDatabase.getInstance(this).universeDao().findItemById(it)
        }

        val limitModel = LimitModel(
            limit.id,
            limit.name,
            limit.minValue ?: 0,
            limit.maxValue ?: 0,
            limit.universeId!!,
            universe?.name!!,
            universe.fbdlCommandItemId!!)

        val newFragment = EditLimitFragment.newInstance(limitModel)
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