package org.example.todo.model.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "todo")
@Parcelize
data class ToDoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val dueDate: LocalDate,
    val priority: Priority,
    val description: String,
) : Parcelable
