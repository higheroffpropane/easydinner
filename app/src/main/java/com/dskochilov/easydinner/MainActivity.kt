package com.dskochilov.easydinner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dskochilov.easydinner.presentation.navigation.EasyDinnerRoot
import com.dskochilov.easydinner.presentation.theme.EasyDinnerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyDinnerTheme {
                EasyDinnerRoot()
            }
        }
    }
}
