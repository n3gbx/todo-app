package org.example.todo.model.repository

import androidx.lifecycle.LiveData
import org.example.todo.model.data.dao.ToDoDao
import org.example.todo.model.data.entity.ToDoEntity
import javax.inject.Inject

class ToDoRepository @Inject constructor(
    private val toDoDao: ToDoDao
) {

    fun getAllToDo(): LiveData<List<ToDoEntity>> = toDoDao.getAll()

    fun getToDo(id: Int): LiveData<ToDoEntity> = toDoDao.getOne(id)

    suspend fun createToDo(toDo: ToDoEntity) = toDoDao.insertOne(toDo)

    suspend fun updateToDo(toDo: ToDoEntity) = toDoDao.updateOne(toDo)

    suspend fun deleteOne(toDoId: Int) = toDoDao.deleteOne(toDoId)

    fun search(query: String): LiveData<List<ToDoEntity>> = toDoDao.search("%${query}%")

    fun sortByPriorityDescending(): LiveData<List<ToDoEntity>> = toDoDao.sortByPriorityDescending()

    fun sortByPriorityAscending(): LiveData<List<ToDoEntity>> = toDoDao.sortByPriorityAscending()
}