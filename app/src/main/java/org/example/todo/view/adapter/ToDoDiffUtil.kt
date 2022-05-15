package org.example.todo.view.adapter

import androidx.recyclerview.widget.DiffUtil
import org.example.todo.model.data.entity.ToDoEntity

class ToDoDiffUtil(
    private val newList: List<ToDoEntity>,
    private val oldList: List<ToDoEntity>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
                && oldList[oldItemPosition].description == newList[newItemPosition].description
                && oldList[oldItemPosition].dueDate == newList[newItemPosition].dueDate
                && oldList[oldItemPosition].priority == newList[newItemPosition].priority
    }
}