package com.example.robotcontroller.universe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.robotcontroller.R
import com.example.robotcontroller.universe.OnInputListener

class EditUniverseFragment : DialogFragment() {

    var mOnInputListener: OnInputListener? = null

    companion object {
        const val UNIVERSE_NAME: String = "UNIVERSE_NAME"

        @JvmStatic
        fun newInstance(param1: String) =
            EditUniverseFragment().apply {
                arguments = Bundle().apply {
                    putString(UNIVERSE_NAME, param1)
                }
            }
    }

    private lateinit var universeNameEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            mOnInputListener = activity as OnInputListener?
        } catch (e: ClassCastException) {
            Log.e("EditUniverseFragment", "onAttach: ClassCastException: ${e.message}")
        }
        val v: View = inflater.inflate(R.layout.fragment_universe_layout, container, false)

        v.findViewById<Button>(R.id.btn_done_edit_universe_fragment).setOnClickListener {
            val name = universeNameEditText.text
            mOnInputListener?.sendInput(name.toString())
            mOnInputListener?.setUniverseToNull()

            dialog?.dismiss()
        }

        v.findViewById<Button>(R.id.btn_cancel_edit_universe_fragment).setOnClickListener {
            dialog?.dismiss()
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setTitle("Create universe")
        universeNameEditText = dialog?.findViewById<EditText>(R.id.edit_universe_name_fragment)!!
        universeNameEditText.requestFocus();

        arguments?.let { it1 ->
            universeNameEditText.setText(it1.getString(UNIVERSE_NAME))
        }

    }
}