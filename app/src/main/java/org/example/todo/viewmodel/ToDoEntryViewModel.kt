package org.example.todo.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.todo.model.data.entity.Priority
import org.example.todo.model.data.entity.ToDoEntity
import org.example.todo.model.repository.ToDoRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ToDoEntryViewModel @Inject constructor(
    private val repository: ToDoRepository
): ViewModel() {
    val titleData = MutableLiveData<String>()
    val descriptionData = MutableLiveData<String>()
    val dueDateData = MutableLiveData<String>()
    val priorityData = MutableLiveData<String>()

    val formValidationState = MediatorLiveData<Boolean>()

    init {
        formValidationState.addSource(titleData) { validateForm() }
        formValidationState.addSource(descriptionData) { validateForm() }
        formValidationState.addSource(dueDateData) { validateForm() }
        formValidationState.addSource(priorityData) { validateForm() }
    }

    fun getToDo(id: Int): LiveData<ToDoEntity> {
        return repository.getToDo(id)
    }

    fun deleteToDo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteOne(id)
        }
    }

    fun updateToDo(id: Int, title: String, description: String, dueDate: String, priority: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateToDo(
                ToDoEntity(
                    id = id,
                    title = title,
                    description = description,
                    dueDate = LocalDate.parse(dueDate),
                    priority = Priority.valueOf(priority.uppercase()),
                )
            )
        }
    }

    fun createToDo(title: String, description: String, dueDate: String, priority: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createToDo(
                ToDoEntity(
                    title = title,
                    description = description,
                    dueDate = LocalDate.parse(dueDate),
                    priority = Priority.valueOf(priority.uppercase()),
                )
            )
        }
    }

    private fun validateForm() {
        val titleValid = titleData.value?.isNotBlank() ?: false
        val descriptionValid = descriptionData.value?.isNotBlank() ?: false
        val dueDateValid = dueDateData.value?.isNotBlank() ?: false
        val priorityValid = priorityData.value?.isNotBlank() ?: false

        formValidationState.value = titleValid && descriptionValid && dueDateValid && priorityValid
    }
}