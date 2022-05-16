package org.example.todo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.example.todo.model.data.entity.ToDoEntity

@Parcelize
data class ToDoActionContent(
    val action: ToDoAction,
    val content: ToDoEntity,
): Parcelable
