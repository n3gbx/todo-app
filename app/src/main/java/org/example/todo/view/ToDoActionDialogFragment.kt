package org.example.todo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.example.todo.databinding.BottomDialogBinding
import org.example.todo.model.ToDoAction
import org.example.todo.model.ToDoActionContent
import org.example.todo.model.data.entity.ToDoEntity
import org.example.todo.util.sentenceCase
import org.example.todo.viewmodel.SharedViewModel
import java.util.*


class ToDoActionDialogFragment: BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var toDoContent: ToDoEntity
    private lateinit var binding: BottomDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toDoContent = arguments!!.getParcelable(TODO_CONTENT_KEY)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomDialogBinding.inflate(inflater, container, false)

        binding.actionShare.text = ToDoAction.SHARE.name.sentenceCase()
        binding.actionShare.setOnClickListener(this)

        binding.actionCopy.text = ToDoAction.COPY.name.sentenceCase()
        binding.actionCopy.setOnClickListener(this)

        binding.actionEdit.text = ToDoAction.EDIT.name.sentenceCase()
        binding.actionEdit.setOnClickListener(this)

        binding.actionDelete.text = ToDoAction.DELETE.name.sentenceCase()
        binding.actionDelete.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(actionView: View?) {
        val res = ToDoActionContent(
            action = ToDoAction.valueOf((actionView as TextView).text.toString().uppercase()),
            content = toDoContent
        )
        setFragmentResult(REQ_KEY, bundleOf(TODO_CONTENT_KEY to res))

        dismiss()
    }

    companion object {
        const val REQ_KEY = "req_key"
        const val TODO_CONTENT_KEY = "todo_content_req_key"
    }
}