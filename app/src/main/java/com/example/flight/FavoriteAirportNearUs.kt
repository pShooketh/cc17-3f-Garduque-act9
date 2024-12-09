package com.example.flight

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteAirportNearUs {
    @Query("""
        SELECT * FROM favorite 
        WHERE departure_code = :depCode 
        AND destination_code = :destCode
    """)
    suspend fun getFavorite(depCode: String, destCode: String): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("""
        SELECT 
            f.departure_code as departureCode,
            a1.name as departureName,
            f.destination_code as destinationCode,
            a2.name as destinationName
        FROM favorite f
        JOIN airport a1 ON f.departure_code = a1.iata_code
        JOIN airport a2 ON f.destination_code = a2.iata_code
    """)
    fun getAllFavoritesWithAirportInfo(): Flow<List<FavoriteAirport>>
}
