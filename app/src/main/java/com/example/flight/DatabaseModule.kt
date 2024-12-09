package com.example.flight

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FlightDatabase {
        return FlightDatabase.getDatabase(context)
    }

    @Provides
    fun provideAirportDao(database: FlightDatabase): AirportNearUs {
        return database.airportDao()
    }

    @Provides
    fun provideFavoriteDao(database: FlightDatabase): FavoriteAirportNearUs {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideAirportRepository(
        airportNearUs: AirportNearUs,
        favoriteAirportNearUs: FavoriteAirportNearUs
    ): AirportRepository {
        return AirportRepositoryImpl(airportNearUs, favoriteAirportNearUs)
    }
}
