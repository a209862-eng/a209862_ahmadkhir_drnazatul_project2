package com.example.a209862_ahmadkhir_drnazatulaini_project2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ─────────────────────────────────────────────────────────────────────────────
// ROOM DATABASE
// (Lab 5, Part 2 – Required Component 3: Room Database with Singleton)
//
//  • entities  = all @Entity classes this database manages
//  • version   = bump this whenever you change the schema
//  • exportSchema = false for simple student projects (avoids extra file)
// ─────────────────────────────────────────────────────────────────────────────
@Database(
    entities       = [AduanEntity::class],
    version        = 1,
    exportSchema   = false
)
abstract class AppDatabase : RoomDatabase() {

    /** Expose the DAO so the repository can call it. */
    abstract fun aduanDao(): AduanDao

    companion object {
        // Volatile: writes to INSTANCE are immediately visible to all threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance, creating it if it does not exist yet.
         * Using synchronized { } ensures only one thread can enter the
         * creation block at a time (prevents duplicate databases on race).
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"          // SQLite file name on-device
                )
                    // fallbackToDestructiveMigration: during development it is
                    // acceptable to wipe data when the schema changes.
                    // Replace with proper Migration objects before production.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
