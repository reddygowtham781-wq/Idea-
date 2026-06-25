package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.IdeaDatabase
import com.example.data.IdeaRepository
import com.example.ui.IdeaDashboard
import com.example.ui.IdeaViewModel
import com.example.ui.IdeaViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: IdeaDatabase
    private lateinit var repository: IdeaRepository
    private lateinit var viewModel: IdeaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room Database, DAO and Repository
        database = Room.databaseBuilder(
            applicationContext,
            IdeaDatabase::class.java, "ideaflow-db"
        ).fallbackToDestructiveMigration() // Graceful upgrade pattern
         .build()

        repository = IdeaRepository(
            ideaDao = database.ideaDao(),
            workStepDao = database.workStepDao(),
            mistakeDao = database.mistakeDao(),
            commentDao = database.commentDao()
        )

        // Initialize ViewModel using the Factory pattern
        val factory = IdeaViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[IdeaViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    IdeaDashboard(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

