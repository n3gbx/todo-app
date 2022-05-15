package org.example.todo.model.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.example.todo.model.data.dao.ToDoDao
import org.example.todo.model.data.entity.ToDoEntity

@Database(
    entities = [ToDoEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(value = [Converter::class])
abstract class ToDoDatabase: RoomDatabase() {

    abstract fun getToDoDao(): ToDoDao
}