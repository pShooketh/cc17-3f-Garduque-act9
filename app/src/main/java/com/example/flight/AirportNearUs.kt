package com.example.flight

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportNearUs {
    @Query("""
        SELECT * FROM airport 
        WHERE iata_code LIKE '%' || :query || '%' 
        OR name LIKE '%' || :query || '%'
    """)
    fun searchAirports(query: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :code LIMIT 1")
    suspend fun getAirportByCode(code: String): Airport?
}
