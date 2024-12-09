package com.example.flight

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AirportRepository {
    fun searchAirports(query: String): Flow<List<Airport>>
    fun getAllFavorites(): Flow<List<FavoriteAirport>>
    suspend fun toggleFavorite(favorite: Favorite)
}

class AirportRepositoryImpl @Inject constructor(
    private val airportNearUs: AirportNearUs,
    private val favoriteAirportNearUs: FavoriteAirportNearUs
) : AirportRepository {
    override fun searchAirports(query: String): Flow<List<Airport>> =
        airportNearUs.searchAirports("%$query%")

    override fun getAllFavorites(): Flow<List<FavoriteAirport>> =
        favoriteAirportNearUs.getAllFavoritesWithAirportInfo()

    override suspend fun toggleFavorite(favorite: Favorite) {
        val existing = favoriteAirportNearUs.getFavorite(favorite.departureCode, favorite.destinationCode)
        if (existing == null) {
            favoriteAirportNearUs.insert(favorite)
        } else {
            favoriteAirportNearUs.delete(existing)
        }
    }
}