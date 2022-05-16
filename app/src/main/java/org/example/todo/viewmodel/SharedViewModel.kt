package org.example.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.example.todo.model.ToDoAction
import org.example.todo.model.ToDoActionContent

class SharedViewModel: ViewModel() {
    private var _toDoSelectedDate = MutableLiveData<String>("")
    val toDoSelectedDate: LiveData<String> get() = _toDoSelectedDate

    fun onDueDateSelect(date: String) {
        _toDoSelectedDate.value = date
    }
}