package com.example.myapplicationtestsade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.myapplicationtestsade.ui.theme.MyApplicationTestSadeTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.parameters
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTestSadeTheme {
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }*/
                MainContant(modifier = Modifier)
            }
        }
    }
    @Composable
    fun MainContant(modifier: Modifier = Modifier){
        Column{
            var baconIpsum by remember { mutableStateOf("") }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // zum vertikalen Scrollen
            ){
                var textValue by remember { mutableStateOf("") }
                TextField(
                    value = textValue,
                    onValueChange = { new-> textValue = new }
                )
                Button(
                    onClick = {
                        lifecycleScope.launch {
                            baconIpsum = client.request {
                                method = HttpMethod.Get
                                url {
                                    parameters{
                                        //append("parse", textValue)
                                        append("results", "30")
                                    }
                                }
                            }.bodyAsText()
                        }
                    }
                ) {
                    Text("Send")
                }
            }
            Text(text = baconIpsum)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTestSadeTheme {
        Greeting("Android")
    }
}

//1. HttpClient erstellen
val client = HttpClient(CIO) {
    // Für den Fall des Aufrufens von Calls mit JSON Responses
    // install(ContentNegotiation)
    install(DefaultRequest) {
        url("https://baconipsum.com/api/?type=meat-and-filler")
        //url("https://randomuser.me/api/")
    }
}

/*
Http Endpunkt definieren,

Endpunkt aufrufen bei Button Press,

UI aktuallisiern,
*/
