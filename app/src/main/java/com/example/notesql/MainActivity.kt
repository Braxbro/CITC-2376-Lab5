package com.example.notesql

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesql.ui.NoteSqlApp
import com.example.notesql.ui.NoteSqlViewModel
import com.example.notesql.ui.theme.NoteSQLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ViewModel is placed at top level to control theme
            val viewModel: NoteSqlViewModel =
                viewModel(factory = NoteSqlViewModel.Factory)
            Surface {
                NoteSQLTheme(
                    // Don't override default dark theme setting without confirmation first
                    darkTheme = viewModel.themeOverride.collectAsState().value
                        ?: isSystemInDarkTheme()
                ) {
                    NoteSqlApp(viewModel)
                }
            }
        }
    }
}
