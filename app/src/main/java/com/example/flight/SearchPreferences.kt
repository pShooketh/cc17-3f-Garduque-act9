package com.example.flight

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_preferences")

class SearchPreferences @Inject constructor(
    private val context: Context,
    private val airportNearUs: AirportNearUs
) {
    private val searchQueryKey = stringPreferencesKey("search_query")
    private val recentSearchesKey = stringPreferencesKey("recent_searches")

    val searchQuery: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[searchQueryKey] ?: ""
        }

    val recentSearches: Flow<List<Airport>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[recentSearchesKey]?.split(",")
                ?.filter { it.isNotEmpty() }
                ?.mapNotNull { code ->
                    airportNearUs.getAirportByCode(code)
                } ?: emptyList()
        }

    suspend fun saveSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            preferences[searchQueryKey] = query
        }
    }

    suspend fun addRecentSearch(airport: Airport) {
        context.dataStore.edit { preferences ->
            val current = (preferences[recentSearchesKey] ?: "")
                .split(",")
                .filter { it.isNotEmpty() }
            val updated = (listOf(airport.iataCode) + current)
                .distinct()
                .take(5)
            preferences[recentSearchesKey] = updated.joinToString(",")
        }
    }
}
