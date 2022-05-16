package org.example.todo.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.example.todo.R
import org.example.todo.databinding.FragmentEntryBinding
import org.example.todo.model.data.entity.Priority
import org.example.todo.viewmodel.ToDoEntryViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import org.example.todo.MainActivity
import org.example.todo.viewmodel.SharedViewModel


@AndroidEntryPoint
class ToDoEntryFragment : Fragment() {
    private val viewModel: ToDoEntryViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private val args by navArgs<ToDoEntryFragmentArgs>()

    private lateinit var binding: FragmentEntryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val toolbar = binding.toolbar
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        // If we arrived here with an toDoId of > 0, then we are editing an existing item
        if (args.toDoId > 0) {
            setHasOptionsMenu(true)

            with(resources.getString(R.string.label_update)) {
                (requireActivity() as AppCompatActivity).supportActionBar?.title = this
                binding.buttonSubmit.text = this
            }

            viewModel.getToDo(args.toDoId).observe(viewLifecycleOwner, { toDo ->
                binding.inputTextTitle.setText(toDo.title)
                binding.inputTextDescription.setText(toDo.description)
                binding.inputTextPriority.setText(toDo.priority.name.lowercase(), false)
                binding.inputTextDueDate.setText(toDo.dueDate.toString())
            })

            binding.buttonSubmit.setOnClickListener {
                updateToDo(args.toDoId)
            }
        } else {
            with(resources.getString(R.string.label_create)) {
                (requireActivity() as AppCompatActivity).supportActionBar?.title = this
                binding.buttonSubmit.text = this
            }

            binding.buttonSubmit.setOnClickListener {
                createToDo()
            }
        }

        binding.inputTextPriority.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_prirority,
                Priority.values().map { it.name.lowercase() })
        )

        binding.inputTextDueDate.setOnClickListener {
            DatePickerDialogFragment().show(childFragmentManager, DatePickerDialogFragment::class.java.canonicalName)
        }

        sharedViewModel.toDoSelectedDate.observe(viewLifecycleOwner, {
            binding.inputTextDueDate.setText(it)
        })

        viewModel.formValidationState.observe(viewLifecycleOwner, { validationResult ->
            binding.buttonSubmit.isEnabled = validationResult
        })

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteToDo(args.toDoId)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.fragment_update_menu, menu)
    }

    override fun onDetach() {
        super.onDetach()
        // TODO
        sharedViewModel.onDueDateSelect("")
    }

    private fun deleteToDo(id: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.label_warning)
            .setMessage(R.string.label_warning_delete_message)
            .setNegativeButton(R.string.label_no) { _, _ -> }
            .setPositiveButton(R.string.label_yes) { _, _ ->
                viewModel.deleteToDo(id)
                findNavController().popBackStack()
            }.create().show()
    }

    private fun updateToDo(id: Int) {
        viewModel.updateToDo(
            id = id,
            title = binding.inputTextTitle.text.toString(),
            description = binding.inputTextDescription.text.toString(),
            dueDate = binding.inputTextDueDate.text.toString(),
            priority = binding.inputTextPriority.text.toString(),
        )
        findNavController().popBackStack()
    }

    private fun createToDo() {
        viewModel.createToDo(
            title = binding.inputTextTitle.text.toString(),
            description = binding.inputTextDescription.text.toString(),
            dueDate = binding.inputTextDueDate.text.toString(),
            priority = binding.inputTextPriority.text.toString(),
        )
        findNavController().popBackStack()
    }
}