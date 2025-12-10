package com.example.mobileproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.mobileproject.ui.theme.MobileProjectTheme
import data.Task
import data.TaskDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Get Database
        val database = TaskDatabase.getDatabase(this)

        // 2. Create Repository (Wrapper around DAO)
        val repository = TaskRepository(database.taskDao())

        // 3. Create ViewModel Factory with Repository
        val viewModelFactory = TaskViewModelFactory(repository)

        // 4. Get ViewModel
        val viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        setContent {
            MobileProjectTheme {
                TaskScreen(viewModel) { taskTitle ->
                    openTaskDetail(taskTitle)
                }
            }
        }
    }

    private fun openTaskDetail(taskTitle: String) {
        // 1. Create Intent pointing explicitly to DetailActivity
        val intent = Intent(this, DetailActivity::class.java)

        // 2. Pass data
        intent.putExtra("TASK_TITLE", taskTitle)

        // 3. Launch
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel, onDetailClick: (String) -> Unit) {
    val tasks by viewModel.tasks.collectAsState()
    var textInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Smart To Do") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Enter a new task") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.addTask(textInput)
                        textInput = ""
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task) },
                        onDetail = { onDetailClick(task.title) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onDetail: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.clickable { onToggle() }) {
                Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
                Text(
                    text = task.title,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Row {
                // Info Icon triggers Explicit Intent
                IconButton(onClick = onDetail) {
                    Icon(Icons.Default.Info, contentDescription = "View Details", tint = Color.Blue)
                }
                // Delete Icon
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}