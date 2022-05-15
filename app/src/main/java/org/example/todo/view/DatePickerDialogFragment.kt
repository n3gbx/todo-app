package org.example.todo.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import org.example.todo.viewmodel.SharedViewModel
import java.time.LocalDate
import java.util.*

class DatePickerDialogFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.onDueDateSelect(LocalDate.of(year, month + 1, day).toString())
    }
}