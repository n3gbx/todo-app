package org.example.todo.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import org.example.todo.model.data.entity.Priority
import org.example.todo.util.observeOnce


@AndroidEntryPoint
class ToDoListFragment : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var binding: FragmentListBinding
    private val listAdapter: ToDoListAdapter by lazy { ToDoListAdapter() }
    private val viewModel: ToDoListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = FragmentListBinding.inflate(inflater, container, false)

        setupRecyclerView(binding.recyclerViewList)
        setupToolbar(binding.toolbar)

        binding.fabCreate.setOnClickListener {
            findNavController().navigate(
                ToDoListFragmentDirections.actionListFragmentToEntryFragment(-1)
            )
        }

        listAdapter.setOnItemClickListener(object : ToDoListAdapter.OnItemClickListener {
            override fun onItemLongClick(toDo: ToDoEntity, pos: Int): Boolean {
                return true
            }

            override fun onItemClick(toDo: ToDoEntity, pos: Int) {
                findNavController().navigate(
                    ToDoListFragmentDirections.actionListFragmentToEntryFragment(toDo.id)
                )
            }
        })

        viewModel.toDoList.observe(viewLifecycleOwner, { list ->
            with(if (list.isEmpty()) VISIBLE else GONE) {
                binding.imageAllSet.visibility = this
                binding.textAllSet.visibility = this
            }

            listAdapter.setList(list)
            binding.recyclerViewList.scheduleLayoutAnimation()
        })

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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return searchOnQuery(query)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return searchOnQuery(newText)
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
                        listAdapter.notifyDataSetChanged()
                    }
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}