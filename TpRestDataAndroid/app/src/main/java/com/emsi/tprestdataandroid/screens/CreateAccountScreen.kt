package com.emsi.tprestdataandroid.screens

import Compte
import MainViewModel
import TypeCompte
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateAccountScreen(viewModel: MainViewModel) {
    var balance by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf(TypeCompte.COURANT) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = sdf.format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Créer un compte",
            style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF388E3C))
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = balance,
            onValueChange = { balance = it },
            label = { Text("Solde") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Date de création : $formattedDate",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF388E3C))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (balance.isEmpty()) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Le solde ne peut pas être vide.")
                    }
                    return@Button
                }

                val balanceAmount = balance.toDoubleOrNull()?.let {
                    if (it >= 0) "%.2f".format(it).toDouble() else null
                } ?: run {
                    scope.launch {
                        snackbarHostState.showSnackbar("Valeur de solde invalide.")
                    }
                    return@Button
                }

                val compteToSave = Compte(
                    id = 0,
                    solde = balanceAmount,
                    dateCreation = formattedDate,
                    type = accountType
                )

                scope.launch {
                    val response = if (viewModel.contentType == "application/json") {
                        viewModel.apiService.createCompteJson(compteToSave)
                    } else {
                        viewModel.apiService.createCompteXml(compteToSave)
                    }
                    if (response.isSuccessful) {
                        snackbarHostState.showSnackbar("Compte créé avec succès.")
                    } else {
                        snackbarHostState.showSnackbar("Erreur lors de la création du compte.")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color(0xFF4CAF50))
        ) {
            Text(
                text = "Créer un compte",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SnackbarHost(hostState = snackbarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountTypeDropdown(accountType: TypeCompte, onAccountTypeChange: (TypeCompte) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = accountType.name,
            onValueChange = {},
            label = { Text("Type de compte") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("COURANT") },
                onClick = { onAccountTypeChange(TypeCompte.COURANT); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("EPARGNE") },
                onClick = { onAccountTypeChange(TypeCompte.EPARGNE); expanded = false }
            )
        }
    }
}
