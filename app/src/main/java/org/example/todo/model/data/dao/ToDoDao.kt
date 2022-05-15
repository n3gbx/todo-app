package org.example.todo.model.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.example.todo.model.data.entity.ToDoEntity

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo ORDER BY id ASC")
    fun getAll(): LiveData<List<ToDoEntity>>

    @Query("SELECT * FROM todo WHERE id == :id")
    fun getOne(id: Int): LiveData<ToDoEntity>

    @Update
    suspend fun updateOne(toDoEntity: ToDoEntity)

    @Query("DELETE FROM todo WHERE id == :id")
    suspend fun deleteOne(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOne(toDoEntity: ToDoEntity)

    @Query("SELECT * FROM todo WHERE title LIKE :query")
    fun search(query: String): LiveData<List<ToDoEntity>>

    @Query("SELECT * FROM todo ORDER BY CASE WHEN priority LIKE 'U%' THEN 1 WHEN priority LIKE 'N%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun sortByPriorityDescending(): LiveData<List<ToDoEntity>>

    @Query("SELECT * FROM todo ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'N%' THEN 2 WHEN priority LIKE 'U%' THEN 3 END")
    fun sortByPriorityAscending(): LiveData<List<ToDoEntity>>
}