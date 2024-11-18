package com.emsi.tprestdataandroid.screens

import Compte
import MainViewModel
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emsi.tprestdataandroid.state.UiState

@Composable
fun EmptyStateScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF388E3C)),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error: $message",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFD32F2F)),
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text(text = "Retry", color = Color.White)
            }
        }
    }
}

@Composable
fun AccountListScreen(viewModel: MainViewModel, navController: NavController) {
    val comptesState by viewModel.comptes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchComptes()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (comptesState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Success -> {
                val comptes = (comptesState as UiState.Success).data
                Log.d("MainViewModel", "Comptes: $comptes")
                if (comptes.isEmpty()) {
                    EmptyStateScreen(message = "No accounts available.")
                } else {
                    AccountList(
                        comptes = comptes,
                        onItemSelected = { selectedCompte ->
                            Log.d("AccountList", "Selected account: ${selectedCompte.id}")
                        },
                        onDeleteClick = { compte ->
                            compte.id?.let {
                                viewModel.deleteCompte(it)
                            } ?: run {
                                Log.e("AccountList", "Account ID is null, cannot delete.")
                            }
                        },
                        onEditClick = { compte ->
                            navController.navigate("update_account/${compte.id}")
                        }
                    )
                }
            }

            is UiState.Error -> ErrorScreen(message = (comptesState as UiState.Error).message) {
                viewModel.fetchComptes()
            }
        }
    }
}

@Composable
fun AccountList(
    comptes: List<Compte>,
    onItemSelected: (Compte) -> Unit,
    onDeleteClick: (Compte) -> Unit,
    onEditClick: (Compte) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(comptes) { compte ->
            AccountListItem(compte, onItemSelected, onDeleteClick, onEditClick)
        }
    }
}

@Composable
fun AccountListItem(
    compte: Compte,
    onItemSelected: (Compte) -> Unit,
    onDeleteClick: (Compte) -> Unit,
    onEditClick: (Compte) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onItemSelected(compte) }
            .shadow(4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Compte N° ${compte.id}",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF388E3C))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Solde: ${String.format("%.2f", compte.solde)}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Date de création: ${compte.dateCreation}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Type du compte: ${compte.type.name}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onEditClick(compte) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Account",
                        tint = Color(0xFF388E3C)
                    )
                }
                IconButton(onClick = { onDeleteClick(compte) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Account",
                        tint = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}

