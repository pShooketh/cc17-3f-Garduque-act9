package com.example.flight

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Airport::class, Favorite::class], version = 2, exportSchema = false)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportNearUs
    abstract fun favoriteDao(): FavoriteAirportNearUs

    companion object {
        @Volatile
        private var instance: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightDatabase::class.java,
                    "flight_database"
                )
                    .createFromAsset("database/flight_search.db")
                    .fallbackToDestructiveMigration() // Forces schema reconversion
                    .build()
                Companion.instance = instance
                instance
            }
        }
    }

}
