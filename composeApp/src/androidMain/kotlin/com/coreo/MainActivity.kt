package com.coreo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.coreo.data.AppRepository

class MainActivity : ComponentActivity() {
    private lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        repository = AppRepository(applicationContext)
        setContent {
            App(repository = repository)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(repository = null)
}
