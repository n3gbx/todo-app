package org.example.todo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.example.todo.databinding.BottomDialogBinding
import org.example.todo.model.ToDoAction
import org.example.todo.viewmodel.SharedViewModel


class ToDoActionDialogFragment: BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var binding: BottomDialogBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomDialogBinding.inflate(inflater, container, false)

        binding.actionShare.text = ToDoAction.SHARE.name.lowercase().capitalize()
        binding.actionShare.setOnClickListener(this)

        binding.actionCopy.text = ToDoAction.COPY.name.lowercase().capitalize()
        binding.actionCopy.setOnClickListener(this)

        binding.actionEdit.text = ToDoAction.EDIT.name.lowercase().capitalize()
        binding.actionEdit.setOnClickListener(this)

        binding.actionDelete.text = ToDoAction.DELETE.name.lowercase().capitalize()
        binding.actionDelete.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(actionView: View?) {
        sharedViewModel.onActionSelect(ToDoAction.valueOf((actionView as TextView).text.toString().uppercase()))
        dismiss()
    }
}