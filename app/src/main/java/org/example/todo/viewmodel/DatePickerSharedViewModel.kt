package org.example.todo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DatePickerSharedViewModel: ViewModel() {
    val selectedDate = MutableLiveData("")
}