package com.example.robotcontroller.limit.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.robotcontroller.R
import com.example.robotcontroller.adapter.LimitModel
import com.example.robotcontroller.limit.OnInputListenerForLimit
import com.example.robotcontroller.viewmodels.GenericViewModelFactory
import com.example.robotcontroller.viewmodels.UniverseViewModel

class UniversalSpinnerHolder(
    val id: Long?,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

class EditLimitFragment : DialogFragment() {
    companion object {
        const val NAMEVALUE: String = "NAMEVALUE"
        const val MINVALUE: String = "MINVALUE"
        const val MAXVALUE: String = "MAXVALUE"
        const val UNIVERSE_ID: String = "UNIVERSE_ID"
        const val FBDL_ID: String = "FBDL_ID"

        @JvmStatic
        fun newInstance(limit: LimitModel) =
            EditLimitFragment().apply {
                arguments = Bundle().apply {
                    putString(NAMEVALUE, limit.name)
                    putInt(MINVALUE, limit.minValue)
                    putInt(MAXVALUE, limit.maxValue)
                    limit.universeId?.let { putLong(UNIVERSE_ID, it) }
                    limit.fbdlId?.let { putLong(FBDL_ID, it) }
                }
            }
    }

    private lateinit var spinnerHolders: MutableList<UniversalSpinnerHolder>
    private val universeViewModel by viewModels<UniverseViewModel> {
        GenericViewModelFactory(this.requireContext())
    }

    private lateinit var nameEditText: EditText
    private lateinit var minValueEditText: EditText
    private lateinit var maxValueEditText: EditText
    private lateinit var universeSpinner: Spinner
    private var mOnInputListener: OnInputListenerForLimit? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            mOnInputListener = activity as OnInputListenerForLimit?
        } catch (e: ClassCastException) {
            Log.e("EditLimitFragment", "onAttach: ClassCastException: ${e.message}")
        }
        val view: View = inflater.inflate(R.layout.fragment_edit_limit_layout, container, false)
        nameEditText = view.findViewById(R.id.editLimitName)!!
        minValueEditText = view.findViewById(R.id.editLimitMinValue)!!
        maxValueEditText = view.findViewById(R.id.editLimitMaxValue)!!

        universeSpinner = view.findViewById(R.id.editSelectUniverseSpinner)!!

        arguments?.let { arg ->
            nameEditText.setText(arg.getString(NAMEVALUE))
            minValueEditText.setText(arg.getInt(MINVALUE).toString())
            maxValueEditText.setText(arg.getInt(MAXVALUE).toString())

            universeSpinner = view.findViewById(R.id.editSelectUniverseSpinner)!!

            val fbdlId = arg.getLong(FBDL_ID)!!
            val limitAdapter = createAndGetUniverseArrayAdapter(fbdlId)
            universeSpinner.adapter = limitAdapter

            val universeId = arg.getLong(UNIVERSE_ID)
            val selectedHolder = spinnerHolders.find {  holder ->
                holder.id == universeId
            }

            val selectedItemPosition = limitAdapter.getPosition(selectedHolder)
            universeSpinner.setSelection(selectedItemPosition)
        }

        view.findViewById<Button>(R.id.btn_done_edit_limit_fragment).setOnClickListener {
            val holder = spinnerHolders[universeSpinner.selectedItemId.toInt()]

            mOnInputListener?.sendInput(
                nameEditText.text.toString(),
                maxValueEditText.text.toString().toInt(),
                minValueEditText.text.toString().toInt(),
                holder.id?.toLong() ?: 0
            )
            mOnInputListener?.setToNull()

            dialog?.dismiss()
        }
        view.findViewById<Button>(R.id.btn_cancel_edit_limit_fragment).setOnClickListener {
            dialog?.dismiss()
        }

        return view
    }

    private fun createAndGetUniverseArrayAdapter(fbdlId: Long): ArrayAdapter<UniversalSpinnerHolder> {
        val universes = universeViewModel.getAllByFbdlId(fbdlId)
        spinnerHolders = universes.map {
            UniversalSpinnerHolder(it.id, it.name)
        }.toMutableList()

        return ArrayAdapter(this.requireContext(), R.layout.universe_spinner_item, R.id.universeSpinnerItemName, spinnerHolders)
    }
}