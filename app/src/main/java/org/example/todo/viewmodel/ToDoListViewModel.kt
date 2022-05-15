package org.example.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.todo.model.data.entity.Priority
import org.example.todo.model.data.entity.ToDoEntity
import org.example.todo.model.repository.ToDoRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ToDoListViewModel @Inject constructor(
    private val repository: ToDoRepository
): ViewModel() {

    val toDoList: LiveData<List<ToDoEntity>> = repository.getAllToDo()

    fun onSortByPriorityDescending(): LiveData<List<ToDoEntity>> = repository.sortByPriorityDescending()

    fun onSortByPriorityAscending(): LiveData<List<ToDoEntity>> = repository.sortByPriorityAscending()

    fun searchToDo(query: String): LiveData<List<ToDoEntity>> = repository.search(query)

    fun deleteToDo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteOne(id)
        }
    }

    fun reCreateToDo(toDo: ToDoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createToDo(
                ToDoEntity(
                    title = toDo.title,
                    description = toDo.description,
                    dueDate = toDo.dueDate,
                    priority = toDo.priority,
                )
            )
        }
    }
}