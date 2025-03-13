package com.plataforma.bienestar

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.plataforma.bienestar.ui.theme.BienestarTheme

fun NavigationWrapper(){
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier.clickable {
                throw RuntimeException("P")
            }
        )
    }

    //@Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        BienestarTheme {
            Greeting("Android")
        }
    }
}