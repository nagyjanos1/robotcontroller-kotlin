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
import com.example.robotcontroller.limit.OnInputListenerForLimit
import com.example.robotcontroller.viewmodels.GenericViewModelFactory
import com.example.robotcontroller.viewmodels.UniverseViewModel

class UniverseSpinnerHolder(
    val id: Long?,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

class EditLimitFragment : DialogFragment() {
    companion object {
        const val MINVALUE: String = "MINVALUE"
        const val MAXVALUE: String = "MAXVALUE"
        const val UNIVERSE_ID: String = "UNIVERSE_ID"
        const val FBDL_ID: String = "FBDL_ID"

        @JvmStatic
        fun newInstance(minValue: Int?, maxValue: Int?, universeId: Long?, fbdlId: Long?) =
            EditLimitFragment().apply {
                arguments = Bundle().apply {
                    minValue?.let { putInt(MINVALUE, it) }
                    maxValue?.let { putInt(MAXVALUE, it) }
                    universeId?.let { putLong(UNIVERSE_ID, it) }
                    fbdlId?.let { putLong(FBDL_ID, it) }
                }
            }
    }

    private val universeViewModel by viewModels<UniverseViewModel> {
        GenericViewModelFactory(this.requireContext())
    }

    private lateinit var maxValueEditText: EditText
    private lateinit var minValueEditText: EditText
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
        minValueEditText = view.findViewById(R.id.editLimitMinValue)!!
        maxValueEditText = view.findViewById(R.id.editLimitMaxValue)!!

        universeSpinner = view.findViewById(R.id.editSelectUniverseSpinner)!!

        arguments?.let { it1 ->
            minValueEditText.setText(it1.getInt(MINVALUE).toString())
            maxValueEditText.setText(it1.getInt(MAXVALUE).toString())

            val universes = universeViewModel.getAllByFbdlId(it1.getLong(FBDL_ID))
            val spinnerHolders = universes.map {
                UniverseSpinnerHolder(it.id, it.name)
            }.toMutableList()

            universeSpinner.adapter = ArrayAdapter(this.requireContext(), R.layout.universe_spinner_item, R.id.universeSpinnerItemName, spinnerHolders)
            //universeSpinner.setPromptId(it1.getLong(UNIVERSE_ID).toInt())
        }

        view.findViewById<Button>(R.id.btn_done_edit_limit_fragment).setOnClickListener {

            mOnInputListener?.sendInput(
                minValueEditText.text.toString().toInt(),
                maxValueEditText.text.toString().toInt(),
                minValueEditText.text.toString().toLong()
            )
            mOnInputListener?.setToNull()

            dialog?.dismiss()
        }
        view.findViewById<Button>(R.id.btn_cancel_edit_limit_fragment).setOnClickListener {
            dialog?.dismiss()
        }

        return view
    }
}