package org.example.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.example.todo.model.ToDoAction

class SharedViewModel: ViewModel() {
    private var _toDoSelectedDate = MutableLiveData<String>("")
    val toDoSelectedDate: LiveData<String> get() = _toDoSelectedDate

    private var _toDoAction = MutableLiveData<ToDoAction>()
    val toDoAction: LiveData<ToDoAction> get() = _toDoAction

    fun onActionSelect(action: ToDoAction) {
        _toDoAction.value = action
    }

    fun onDueDateSelect(date: String) {
        _toDoSelectedDate.value = date
    }
}