package com.example.flight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FlightSearchScreen(viewModel: FlightViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        SearchBar(
            value = viewModel.searchQuery.collectAsState().value,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Search Results") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Favorites") }
            )
        }

        when (selectedTabIndex) {
            0 -> SearchResultsList(
                airports = viewModel.searchResults.collectAsState().value,
                onToggleFavorite = { depCode, destCode ->
                    viewModel.toggleFavorite(
                        Favorite(
                        departureCode = depCode,
                        destinationCode = destCode
                    )
                    )
                }
            )
            1 -> FavoritesList(
                favorites = viewModel.favorites.collectAsState().value,
                onRemoveFavorite = viewModel::toggleFavorite
            )
        }
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        placeholder = { Text("Search airports") },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun SearchResultsList(
    airports: List<Airport>,
    onToggleFavorite: (String, String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(airports) { airport ->
            AirportCard(
                airport = airport,
                onFavoriteClick = onToggleFavorite
            )
        }
    }
}

@Composable
fun FavoritesList(
    favorites: List<FavoriteAirport>,
    onRemoveFavorite: (Favorite) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No favorite routes yet",
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites) { favorite ->
                FavoriteRouteCard(
                    favorite = favorite,
                    onDeleteClick = {
                        onRemoveFavorite(
                            Favorite(
                            departureCode = favorite.departureCode,
                            destinationCode = favorite.destinationCode
                        )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AirportCard(
    airport: Airport,
    onFavoriteClick: (String, String) -> Unit
) {
    var showDestinationDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = airport.iataCode,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = airport.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Text(
                    text = "Passengers: ${airport.passengers}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { showDestinationDialog = true }) {
                Icon(Icons.Default.Star, contentDescription = "Add to favorites")
            }
        }
    }

    if (showDestinationDialog) {
        var destinationCode by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDestinationDialog = false },
            title = { Text("Enter Destination Airport Code") },
            text = {
                OutlinedTextField(
                    value = destinationCode,
                    onValueChange = { destinationCode = it.uppercase() },
                    label = { Text("Destination IATA Code") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (destinationCode.isNotEmpty()) {
                            onFavoriteClick(airport.iataCode, destinationCode)
                            showDestinationDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDestinationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FavoriteRouteCard(
    favorite: FavoriteAirport,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = favorite.departureCode,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
                Text(
                    text = favorite.destinationCode,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from favorites",
                    tint = Color.Red
                )
            }
        }
    }
}