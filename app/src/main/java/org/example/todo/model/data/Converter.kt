package org.example.todo.model.data

import androidx.room.TypeConverter
import org.example.todo.model.data.entity.Priority
import java.time.Instant
import java.time.LocalDate

class Converter {

    @TypeConverter
    fun toDate(timestamp: Long): LocalDate {
        return LocalDate.ofEpochDay(timestamp)
    }

    @TypeConverter
    fun fromDate(date: LocalDate): Long {
        return date.toEpochDay()
    }

    @TypeConverter
    fun toPriority(str: String): Priority {
        return Priority.valueOf(str)
    }

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }
}