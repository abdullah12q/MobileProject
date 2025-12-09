package com.example.mobileproject

import kotlinx.coroutines.flow.Flow

import data.Task
import data.TaskDao

// This is the class that will be MOCKED in tests.
// It abstracts the data source (DAO) from the rest of the app.
class TaskRepository(private val dao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> {
        return dao.getAllTasks()
    }

    suspend fun addTask(title: String) {
        val task = Task(title = title)
        dao.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        dao.deleteTask(task)
    }

    suspend fun toggleTaskCompletion(task: Task) {
        dao.updateCompletion(task.id, !task.isCompleted)
    }
}