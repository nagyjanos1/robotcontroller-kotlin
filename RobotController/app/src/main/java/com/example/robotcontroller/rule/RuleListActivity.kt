package com.example.robotcontroller.rule

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
import com.example.robotcontroller.adapter.LimitModel
import com.example.robotcontroller.adapter.RuleAdapter
import com.example.robotcontroller.adapter.RuleModel
import com.example.robotcontroller.data.AppDatabase
import com.example.robotcontroller.data.entities.Rule
import com.example.robotcontroller.rule.fragments.EditRuleFragment
import com.example.robotcontroller.viewmodels.GenericViewModelFactory
import com.example.robotcontroller.viewmodels.RuleViewModel

interface OnInputListenerForRule {
    fun sendInput(name: String,
                  baseUniverseId: Long,
                  baseLimitId: Long,
                  antecedentUniverseId: Long,
                  antecedentLimitId: Long)
    fun setToNull()
}

class RuleListActivity : AppCompatActivity(), OnInputListenerForRule {

    private val newRuleActivityRequestCode = 1
    private lateinit var ruleAdapter: RuleAdapter

    private var latestRuleModel : RuleModel? = null
    private var currentFbdlCommandId: Long? = null

    private val ruleViewModel by viewModels<RuleViewModel> {
        GenericViewModelFactory(this@RuleListActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule_list)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFbdlCommandId = bundle.getLong("itemId")
        }

        ruleAdapter = RuleAdapter {
                ruleItem -> adapterOnClick(ruleItem)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerRule)
        recyclerView.adapter = ruleAdapter

        ruleViewModel.getAll().observe(this){ items ->
            val limitModels = items.map {

                val baseUniverse = AppDatabase.getInstance(this).universeDao().findItemById(it.baseUniverseId!!)
                val antecedentUniverse = AppDatabase.getInstance(this).universeDao().findItemById(it.antecedentUniverseId!!)
                val baseLimit = AppDatabase.getInstance(this).limitsDao().findItemById(it.baseLimitId!!)
                val antecedentLimit = AppDatabase.getInstance(this).limitsDao().findItemById(it.antecedentLimitId!!)

                RuleModel(
                    it.id,
                    it.name,
                    baseUniverse?.id,
                    baseUniverse?.name ?: "",
                    baseLimit?.id,
                    baseLimit?.name ?: "",
                    antecedentUniverse?.id,
                    antecedentUniverse?.name ?: "",
                    antecedentLimit?.id,
                    antecedentLimit?.name ?: ""
                )
            }

            ruleAdapter.submitList(limitModels)
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

                latestRuleModel = RuleModel(
                    null,
                    "",
                    0,
                    "",
                    0,
                    "",
                    0,
                    "",
                    0,
                    "")

                val newFragment = EditRuleFragment.newInstance(latestRuleModel, currentFbdlCommandId)
                newFragment.show(supportFragmentManager, "Set Rule name")
                true
            }
            R.id.btnDelete -> {
                // törlés
                val removableRuleModels = ruleAdapter.getRemovableRules()
                val removableRuleIds = removableRuleModels.map {
                    it.id!!
                }
                val removableRule = ruleViewModel.findItemByIds(removableRuleIds)
                removableRule?.forEach {
                    ruleViewModel.remove(it)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun sendInput(name: String,
                  baseUniverseId: Long,
                  baseLimitId: Long,
                  antecedentUniverseId: Long,
                  antecedentLimitId: Long)
    {
        if (latestRuleModel?.id == null) {
            ruleViewModel.insert(name, baseUniverseId, baseLimitId, antecedentUniverseId, antecedentLimitId)
        } else {
            val rule = Rule(
                latestRuleModel?.id,
                name,
                baseUniverseId,
                baseLimitId,
                antecedentUniverseId,
                antecedentLimitId)
            ruleViewModel.update(rule!!)
        }
    }

    override fun setToNull() {
        latestRuleModel = null
    }

    private fun adapterOnClick(rule: RuleModel) {
        latestRuleModel = rule

        val newFragment = EditRuleFragment.newInstance(rule, currentFbdlCommandId)
        newFragment.show(supportFragmentManager, "Set limit name")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newRuleActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                // Dialógusból visszajövet adatokat csenünk
            }
        }
    }
}