package com.example.flight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@HiltViewModel
class FlightViewModel @Inject constructor(
    private val airportRepository: AirportRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<Airport>>(emptyList())
    val searchResults: StateFlow<List<Airport>> = _searchResults

    private val _favorites = MutableStateFlow<List<FavoriteAirport>>(emptyList())
    val favorites: StateFlow<List<FavoriteAirport>> = _favorites

    init {
        loadFavorites()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        search(query)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            airportRepository.searchAirports(query).collect { airports ->
                _searchResults.value = airports
            }
        }
    }

    fun toggleFavorite(favorite: Favorite) {
        viewModelScope.launch {
            airportRepository.toggleFavorite(favorite)
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            airportRepository.getAllFavorites().collect { favoriteList ->
                _favorites.value = favoriteList
            }
        }
    }
}