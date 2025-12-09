package com.example.mobileproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import data.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// This is the class we will TEST.
// It depends on TaskRepository, which is injected via the constructor.
open class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // Expose data as a Flow
    open val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    open fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(title)
        }
    }

    open fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    open fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }
}

// Factory to inject Repository into ViewModel
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}