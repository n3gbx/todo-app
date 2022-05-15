package org.example.todo.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.example.todo.R
import org.example.todo.databinding.ItemTodoBinding
import org.example.todo.model.data.entity.ToDoEntity
import org.example.todo.view.ToDoListFragmentDirections

class ToDoListAdapter: RecyclerView.Adapter<ToDoListAdapter.ListViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener
    var toDoList: List<ToDoEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding: ItemTodoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_todo,
            parent,
            false
        )

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(toDoList[position])
    }

    override fun getItemCount(): Int {
        return toDoList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(toDoList: List<ToDoEntity>) {
        val diffUtil = ToDoDiffUtil(newList = toDoList, oldList = this.toDoList)
        val toDoDiffCalculateResult = DiffUtil.calculateDiff(diffUtil)
        this.toDoList = toDoList

        toDoDiffCalculateResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class ListViewHolder(private val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root), View.OnLongClickListener, View.OnClickListener {
        private lateinit var item: ToDoEntity

        init {
            binding.root.setOnLongClickListener(this)
            binding.root.setOnClickListener(this)
        }

        fun bind(item: ToDoEntity) {
            this.item = item

            binding.textTitle.text = item.title
            binding.textPriority.text = item.priority.name.lowercase()
            binding.textDescription.text = item.description
        }

        override fun onLongClick(v: View?): Boolean {
            onItemClickListener.onItemLongClick(item, adapterPosition)
            return true
        }

        override fun onClick(v: View?) {
            onItemClickListener.onItemClick(item, adapterPosition)
        }
    }

    interface OnItemClickListener {
        fun onItemLongClick(toDo: ToDoEntity, pos: Int): Boolean
        fun onItemClick(toDo: ToDoEntity, pos: Int)
    }
}