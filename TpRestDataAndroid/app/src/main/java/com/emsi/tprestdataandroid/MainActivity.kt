package com.emsi.tprestdataandroid

import MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emsi.tprestdataandroid.ui.theme.TpRestDataAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = viewModel()

            TpRestDataAndroidTheme {
                NavigationHost(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, mainViewModel: MainViewModel) {
    val formatOptions = listOf("JSON", "XML")
    val selectedFormat = remember { mutableStateOf("JSON") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color(0xFFF1F8E9)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Choisissez une action :",
            style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF388E3C)),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        DropdownMenu(selectedFormat.value, formatOptions) { selected ->
            selectedFormat.value = selected
            mainViewModel.setFormat(selected)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("accounts") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(
                "Afficher la liste des comptes",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("create_account") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
        ) {
            Text(
                "Créer un compte",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun DropdownMenu(
    selectedFormat: String,
    formatOptions: List<String>,
    onFormatSelected: (String) -> Unit
) {
    var expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(onClick = { expanded.value = true }) {
            Text("Format: $selectedFormat")
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            formatOptions.forEach { format ->
                DropdownMenuItem(
                    text = { Text(format) },
                    onClick = {
                        onFormatSelected(format)
                        expanded.value = false
                    }
                )
            }
        }
    }
}
