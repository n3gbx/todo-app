package org.example.todo.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.example.todo.MainActivity
import org.example.todo.R
import org.example.todo.databinding.FragmentListBinding
import org.example.todo.model.data.entity.ToDoEntity
import org.example.todo.view.adapter.ToDoListAdapter
import org.example.todo.viewmodel.ToDoListViewModel
import com.google.android.material.snackbar.Snackbar
import org.example.todo.model.ToDoAction
import org.example.todo.model.data.entity.Priority
import org.example.todo.util.observeOnce
import org.example.todo.viewmodel.SharedViewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import org.example.todo.model.ToDoActionContent


@AndroidEntryPoint
class ToDoListFragment : Fragment(), SearchView.OnQueryTextListener, FragmentResultListener {
    private lateinit var binding: FragmentListBinding
    private val listAdapter: ToDoListAdapter by lazy { ToDoListAdapter() }
    private val viewModel: ToDoListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = FragmentListBinding.inflate(inflater, container, false)

        setupRecyclerView(binding.recyclerViewList)
        setupToolbar(binding.toolbar)

        binding.fabCreate.setOnClickListener {
            navigateCreateToDoFragment()
        }

        listAdapter.setOnItemClickListener(object : ToDoListAdapter.OnItemClickListener {
            override fun onItemLongClick(toDo: ToDoEntity, pos: Int): Boolean {
                val bundle = Bundle()
                bundle.putParcelable(ToDoActionDialogFragment.TODO_CONTENT_KEY, toDo)

                val toDoActionDialogFragment = ToDoActionDialogFragment()
                toDoActionDialogFragment.arguments = bundle
                toDoActionDialogFragment.show(childFragmentManager, ToDoActionDialogFragment::class.java.canonicalName)

                return true
            }

            override fun onItemClick(toDo: ToDoEntity, pos: Int) {
                navigateUpdateToDoFragment(toDo.id)
            }
        })

        viewModel.toDoList.observe(viewLifecycleOwner, { list ->
            with(if (list.isEmpty()) VISIBLE else GONE) {
                binding.imageAllSet.visibility = this
                binding.textAllSet.visibility = this
            }

            listAdapter.setList(list)
        })

        // for one-time communication between fragment and child fragment
        childFragmentManager.setFragmentResultListener(ToDoActionDialogFragment.REQ_KEY, viewLifecycleOwner, this)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_list_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Sort by priority")
                    .setSingleChoiceItems(arrayOf(Priority.URGENT.name, Priority.LOW.name), 0, null)
                    .setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface?.dismiss()
                    }
                    .setPositiveButton("Sort") { dialogInterface, _ ->
                        dialogInterface?.dismiss()

                        when ((dialogInterface as AlertDialog).listView.checkedItemPosition) {
                            0 -> {
                                viewModel.onSortByPriorityDescending().observe(viewLifecycleOwner, { list ->
                                    listAdapter.setList(list)
                                })
                            }
                            1 -> {
                                viewModel.onSortByPriorityAscending().observe(viewLifecycleOwner, { list ->
                                    listAdapter.setList(list)
                                })
                            }
                            else -> {
                                // ignore
                            }
                        }
                    }
                    .show()
            }
            R.id.menu_view -> {
                if (binding.recyclerViewList.layoutManager is LinearLayoutManager) {
                    binding.recyclerViewList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    item.setIcon(R.drawable.ic_rows)
                } else {
                    binding.recyclerViewList.layoutManager = LinearLayoutManager(requireActivity())
                    item.setIcon(R.drawable.ic_columns)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFragmentResult(requestKey: String, bundle: Bundle) {
        val res = bundle.getParcelable<ToDoActionContent>(ToDoActionDialogFragment.TODO_CONTENT_KEY)

        when (res?.action) {
            ToDoAction.SHARE -> {
                val shareContent = "${res.content.title}\n\n${res.content.description}"
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareContent)
                }

                startActivity(Intent.createChooser(intent, null))
            }
            ToDoAction.COPY -> {
                val shareContent = "${res.content.title}\n\n${res.content.description}"
                val clipboardManager = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", shareContent)

                clipboardManager.setPrimaryClip(clipData)

                Toast.makeText(requireContext(), "Copied!", Toast.LENGTH_SHORT).show()
            }
            ToDoAction.EDIT -> {
                navigateUpdateToDoFragment(res.content.id)
            }
            ToDoAction.DELETE -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.label_warning)
                    .setMessage(R.string.label_warning_delete_message)
                    .setNegativeButton(R.string.label_no) { _, _ -> }
                    .setPositiveButton(R.string.label_yes) { _, _ ->
                        viewModel.deleteToDo(res.content.id)
                    }.create().show()
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return searchOnQuery(query)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return searchOnQuery(newText)
    }

    private fun navigateUpdateToDoFragment(toDoId: Int) {
        findNavController().navigate(
            ToDoListFragmentDirections.actionListFragmentToEntryFragment(toDoId)
        )
    }

    private fun navigateCreateToDoFragment() {
        findNavController().navigate(
            ToDoListFragmentDirections.actionListFragmentToEntryFragment(-1)
        )
    }

    private fun searchOnQuery(query: String?): Boolean {
        return query?.let {
            viewModel.searchToDo(it).observeOnce(viewLifecycleOwner, { list ->
                listAdapter.setList(list)
            })

            return@let true
        } ?: false
    }

    private fun setupToolbar(toolbar: Toolbar) {
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        val swipeToDeleteCallback = object : SwipeToDelete() {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = listAdapter.toDoList[viewHolder.adapterPosition]

                viewModel.deleteToDo(itemToDelete.id)
                listAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                Snackbar.make(binding.root, "Deleted: ${itemToDelete.title}", Snackbar.LENGTH_SHORT)
                    .setAction("Undo") {
                        viewModel.reCreateToDo(itemToDelete)
                    }
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}