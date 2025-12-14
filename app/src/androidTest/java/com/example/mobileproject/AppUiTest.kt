package com.example.mobileproject

import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import android.content.Intent
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.ext.junit.runners.AndroidJUnit4
import data.Task
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import org.mockito.Mockito.*
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.test.platform.app.InstrumentationRegistry

@RunWith(AndroidJUnit4::class)
class AppUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sampleTasks = listOf(
        Task(1, "Buy Milk", false),
        Task(2, "Walk Dog", true)
    )

    @Test
    fun addTask_callsViewModelAddTask() {
        val mockViewModel = mock(TaskViewModel::class.java)
        val tasksFlow = MutableStateFlow(sampleTasks)
        `when`(mockViewModel.tasks).thenReturn(tasksFlow)

        composeRule.setContent {
            TaskScreen(viewModel = mockViewModel, onDetailClick = {})
        }

        composeRule.onNodeWithText("Enter a new task").performTextInput("New Task")
        composeRule.onNodeWithText("Add").performClick()

        verify(mockViewModel).addTask("New Task")
    }

    @Test
    fun toggleTask_callsViewModelToggle() {
        val mockViewModel = mock(TaskViewModel::class.java)
        val tasksFlow = MutableStateFlow(sampleTasks)
        `when`(mockViewModel.tasks).thenReturn(tasksFlow)

        composeRule.setContent {
            TaskScreen(viewModel = mockViewModel, onDetailClick = {})
        }

        composeRule.onAllNodes(isToggleable())[0].performClick()

        verify(mockViewModel).toggleTask(sampleTasks[0])
    }

    @Test
    fun deleteTask_callsViewModelDelete() {
        val mockViewModel = mock(TaskViewModel::class.java)
        val tasksFlow = MutableStateFlow(sampleTasks)
        `when`(mockViewModel.tasks).thenReturn(tasksFlow)

        composeRule.setContent {
            TaskScreen(viewModel = mockViewModel, onDetailClick = {})
        }

        composeRule.onAllNodesWithContentDescription("Delete")[1]
            .performClick()

        verify(mockViewModel).deleteTask(sampleTasks[1])
    }

    @Test
    fun detailButton_triggersCallback() {
        val mockViewModel = mock(TaskViewModel::class.java)
        val tasksFlow = MutableStateFlow(sampleTasks)
        var clickedTaskTitle: String? = null

        `when`(mockViewModel.tasks).thenReturn(tasksFlow)

        composeRule.setContent {
            TaskScreen(
                viewModel = mockViewModel,
                onDetailClick = { clickedTaskTitle = it }
            )
        }

        composeRule.onAllNodesWithContentDescription("View Details")[0]
            .performClick()

        assert(clickedTaskTitle == "Buy Milk")
    }

    @Test
    fun clickingInfoIcon_navigatesToDetailActivity_withCorrectIntentExtra() {

        Intents.init()

        try {
            val mockViewModel = mock(TaskViewModel::class.java)
            val tasksFlow = MutableStateFlow(sampleTasks)
            `when`(mockViewModel.tasks).thenReturn(tasksFlow)

            composeRule.setContent {
                TaskScreen(
                    viewModel = mockViewModel,
                    onDetailClick = { title ->

                        val context = InstrumentationRegistry.getInstrumentation().targetContext
                        val intent = Intent(context, DetailActivity::class.java).apply {
                            putExtra("TASK_TITLE", title)

                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                )
            }


            composeRule.onAllNodesWithContentDescription("View Details")[0]
                .performClick()


            Intents.intended(hasComponent(DetailActivity::class.java.name))
            Intents.intended(hasExtra("TASK_TITLE", "Buy Milk"))

        } finally {

            Intents.release()
        }
    }
}