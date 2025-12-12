package com.example.mobileproject

import data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @Mock
    lateinit var mockRepository: TaskRepository

    lateinit var viewModel: TaskViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {

        Dispatchers.setMain(testDispatcher)

        MockitoAnnotations.openMocks(this)


        when(mockRepository.getAllTasks()).thenReturn(flowOf(emptyList()))


        viewModel = TaskViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addTask_calls_repository_addTask() = runTest {

        val title = "Buy Milk"


        viewModel.addTask(title)


        verify(mockRepository).addTask(title)
    }

    @Test
    fun addTask_empty_does_not_call_repository() = runTest {

        val emptyTitle = ""

        viewModel.addTask(emptyTitle)


        verify(mockRepository, never()).addTask(anyString())
    }

    @Test
    fun deleteTask_calls_repository_delete() = runTest {

        val task = Task(id = 1, title = "Delete Me")


        viewModel.deleteTask(task)


        verify(mockRepository).deleteTask(task)
    }
}