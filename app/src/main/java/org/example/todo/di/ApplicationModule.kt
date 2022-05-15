package org.example.todo.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.example.todo.model.data.ToDoDatabase
import org.example.todo.model.data.dao.ToDoDao
import org.example.todo.model.repository.ToDoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext applicationContext: Context): ToDoDatabase {
        return Room.databaseBuilder(applicationContext, ToDoDatabase::class.java, "todo-db").build()
    }

    @Singleton
    @Provides
    fun provideToDoDao(db: ToDoDatabase) = db.getToDoDao()

    @Singleton
    @Provides
    fun provideToDoRepository(toDoDao: ToDoDao): ToDoRepository = ToDoRepository(toDoDao)
}