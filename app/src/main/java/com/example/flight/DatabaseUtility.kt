package com.example.flight

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseUtility {
    suspend fun verifyDatabase(context: Context) = withContext(Dispatchers.IO) {
        try {
            val dbFile = context.getDatabasePath("flight_search_database")
            if (!dbFile.exists()) {
                Log.e("Database", "Database file doesn't exist at: ${dbFile.absolutePath}")
                return@withContext false
            }

            SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            ).use { db ->
                db.rawQuery("SELECT COUNT(*) FROM airport", null).use { cursor ->
                    cursor.moveToFirst()
                    val count = cursor.getInt(0)
                    Log.d("Database", "Airport count from direct SQL: $count")
                    count > 0
                }
            }
        } catch (e: Exception) {
            Log.e("Database", "Error verifying database: ${e.message}", e)
            false
        }
    }
}