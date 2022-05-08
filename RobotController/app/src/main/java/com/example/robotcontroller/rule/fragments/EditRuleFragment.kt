package com.example.robotcontroller.rule.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.robotcontroller.R
import com.example.robotcontroller.adapter.RuleModel
import com.example.robotcontroller.limit.fragments.UniversalSpinnerHolder
import com.example.robotcontroller.rule.OnInputListenerForRule
import com.example.robotcontroller.viewmodels.GenericViewModelFactory
import com.example.robotcontroller.viewmodels.LimitViewModel
import com.example.robotcontroller.viewmodels.UniverseViewModel

open class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}

class EditRuleFragment : DialogFragment() {
    companion object {
        const val NAME_VALUE: String = "NAME_VALUE"
        const val BASE_UNIVERSE_ID: String = "BASE_UNIVERSE_ID"
        const val BASE_LIMIT_ID: String = "BASE_LIMIT_ID"
        const val ANTECEDENT_UNIVERSE_ID: String = "ANTECEDENT_UNIVERSE_ID"
        const val ANTECEDENT_LIMIT_ID: String = "ANTECEDENT_LIMIT_ID"
        const val FBDL_ID: String = "FBDL_ID"

        @JvmStatic
        fun newInstance(rule: RuleModel?, fbdlId: Long?) =
            EditRuleFragment().apply {
                arguments = Bundle().apply {
                    putString(NAME_VALUE, rule?.name)
                    rule?.baseUniverseId?.let { putLong(BASE_UNIVERSE_ID, it) }
                    rule?.baseLimitId?.let { putLong(BASE_LIMIT_ID, it) }
                    rule?.antecedentUniverseId?.let { putLong(ANTECEDENT_UNIVERSE_ID, it) }
                    rule?.antecedentLimitId?.let { putLong(ANTECEDENT_LIMIT_ID, it) }
                    fbdlId?.let { putLong(FBDL_ID, it) }
                }
            }
    }

    private lateinit var nameEditText: EditText
    private lateinit var baseUniverseSpinner: Spinner
    private lateinit var baseLimitSpinner: Spinner
    private lateinit var antecedentUniverseSpinner: Spinner
    private lateinit var antecedentLimitSpinner: Spinner

    private lateinit var baseUniverseSpinnerHolders: MutableList<UniversalSpinnerHolder>
    private lateinit var antecedentUniverseSpinnerHolders: MutableList<UniversalSpinnerHolder>
    private lateinit var baseLimitSpinnerHolders: MutableList<UniversalSpinnerHolder>
    private lateinit var antecedentLimitSpinnerHolders: MutableList<UniversalSpinnerHolder>

    private var mOnInputListener: OnInputListenerForRule? = null

    private val universeViewModel by viewModels<UniverseViewModel> {
        GenericViewModelFactory(this.requireContext())
    }
    private val limitViewModel by viewModels<LimitViewModel> {
        GenericViewModelFactory(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            mOnInputListener = activity as OnInputListenerForRule?
        } catch (e: ClassCastException) {
            Log.e("EditLimitFragment", "onAttach: ClassCastException: ${e.message}")
        }

        val view: View = inflater.inflate(R.layout.fragment_edit_rule_layout, container, false)
        nameEditText = view.findViewById(R.id.editRuleName)!!

        baseUniverseSpinner = view.findViewById(R.id.editSelectBaseUniverseSpinner)!!
        baseLimitSpinner = view.findViewById(R.id.editSelectBaseLimitSpinner)!!
        antecedentUniverseSpinner = view.findViewById(R.id.editSelectAntecedentUniverseSpinner)!!
        antecedentLimitSpinner = view.findViewById(R.id.editSelectAntecedentLimitSpinner)!!

        arguments?.let { arg ->
            nameEditText.setText(arg.getString(NAME_VALUE))

            val fbdlId = arg.getLong(FBDL_ID)!!

            val universes = universeViewModel.getAllByFbdlId(fbdlId)
            baseUniverseSpinnerHolders = universes.map {
                UniversalSpinnerHolder(it.id, it.name)
            }.toMutableList()

            val baseUniverseAdapter = ArrayAdapter(this.requireContext(), R.layout.universe_spinner_item, R.id.universeSpinnerItemName, baseUniverseSpinnerHolders)
            baseUniverseSpinner.adapter = baseUniverseAdapter
            setSelectedItemToSpinner(
                arg.getLong(BASE_UNIVERSE_ID),
                baseUniverseSpinner,
                baseUniverseSpinnerHolders,
                baseUniverseAdapter
                )

            baseUniverseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val holder = baseUniverseSpinnerHolders[position]
                    loadBaseLimitSpinnerList(holder.id!!)

                    val basetLimitAdapter = ArrayAdapter(parent.context, R.layout.limit_spinner_layout, R.id.limitSpinnerItemName, baseLimitSpinnerHolders)
                    baseLimitSpinner.adapter = basetLimitAdapter
                    setSelectedItemToSpinner(
                        arg.getLong(BASE_LIMIT_ID),
                        baseLimitSpinner,
                        baseLimitSpinnerHolders,
                        basetLimitAdapter
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }

            loadBaseLimitSpinnerList(arg.getLong(BASE_UNIVERSE_ID))
            val basetLimitAdapter = ArrayAdapter(this.requireContext(), R.layout.limit_spinner_layout, R.id.limitSpinnerItemName, baseLimitSpinnerHolders)
            baseLimitSpinner.adapter = basetLimitAdapter
            setSelectedItemToSpinner(
                arg.getLong(BASE_LIMIT_ID),
                baseLimitSpinner,
                baseLimitSpinnerHolders,
                basetLimitAdapter
            )

            antecedentUniverseSpinnerHolders = universes.map {
                UniversalSpinnerHolder(it.id, it.name)
            }.toMutableList()

            val antecedentUniverseAdapter = ArrayAdapter(this.requireContext(), R.layout.universe_spinner_item, R.id.universeSpinnerItemName, antecedentUniverseSpinnerHolders)
            antecedentUniverseSpinner.adapter = antecedentUniverseAdapter
            setSelectedItemToSpinner(
                arg.getLong(ANTECEDENT_UNIVERSE_ID),
                antecedentUniverseSpinner,
                antecedentUniverseSpinnerHolders,
                antecedentUniverseAdapter
            )

            antecedentUniverseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val holder = antecedentUniverseSpinnerHolders[position]
                    loadAntecedentLimitSpinnerList(holder.id!!)

                    val antecedentLimitAdapter = ArrayAdapter(parent.context, R.layout.limit_spinner_layout, R.id.limitSpinnerItemName, antecedentLimitSpinnerHolders)
                    antecedentLimitSpinner.adapter = antecedentLimitAdapter
                    setSelectedItemToSpinner(
                        arg.getLong(ANTECEDENT_LIMIT_ID),
                        antecedentLimitSpinner,
                        antecedentLimitSpinnerHolders,
                        antecedentLimitAdapter
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }

            loadAntecedentLimitSpinnerList(arg.getLong(ANTECEDENT_UNIVERSE_ID))
            val antecedentLimitAdapter = ArrayAdapter(this.requireContext(), R.layout.limit_spinner_layout, R.id.limitSpinnerItemName, antecedentLimitSpinnerHolders)
            antecedentLimitSpinner.adapter = antecedentLimitAdapter
            setSelectedItemToSpinner(
                arg.getLong(ANTECEDENT_LIMIT_ID),
                antecedentLimitSpinner,
                antecedentLimitSpinnerHolders,
                antecedentLimitAdapter
            )
        }

        view.findViewById<Button>(R.id.btn_done_edit_rule_fragment).setOnClickListener {
            val baseUniverseHolder = baseUniverseSpinnerHolders[baseUniverseSpinner.selectedItemId.toInt()]
            val antecedentUniverseHolder = baseUniverseSpinnerHolders[antecedentUniverseSpinner.selectedItemId.toInt()]
            val baseLimitHolder = baseLimitSpinnerHolders[baseLimitSpinner.selectedItemId.toInt()]
            val antecedentLimitHolder = antecedentLimitSpinnerHolders[antecedentLimitSpinner.selectedItemId.toInt()]

            mOnInputListener?.sendInput(
                nameEditText.text.toString(),
                baseUniverseHolder.id ?: 0,
                baseLimitHolder.id ?: 0,
                antecedentUniverseHolder.id ?: 0,
                antecedentLimitHolder.id ?: 0
            )
            mOnInputListener?.setToNull()

            dialog?.dismiss()
        }
        view.findViewById<Button>(R.id.btn_cancel_edit_rule_fragment).setOnClickListener {
            dialog?.dismiss()
        }

        return view
    }

    private fun loadBaseLimitSpinnerList(id: Long) {
        val limits = limitViewModel.getAllByUniverseId(id)
        baseLimitSpinnerHolders = limits.map {
            UniversalSpinnerHolder(it.id, it.name)
        }.toMutableList()
    }

    private fun loadAntecedentLimitSpinnerList(id: Long) {
        val limits = limitViewModel.getAllByUniverseId(id)
        antecedentLimitSpinnerHolders = limits.map {
            UniversalSpinnerHolder(it.id, it.name)
        }.toMutableList()
    }

    private fun setSelectedItemToSpinner(
        id: Long,
        spinner: Spinner,
        holders: MutableList<UniversalSpinnerHolder>,
        adapter: ArrayAdapter<UniversalSpinnerHolder>) {
        val selectedHolder = holders.find { holder ->
            holder.id == id
        }
        val selectedItemPosition = adapter.getPosition(selectedHolder)
        spinner.setSelection(selectedItemPosition)
    }

}