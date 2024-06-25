package com.cst.cstacademy2024.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.models.Place
import com.cst.cstacademy2024.models.PlaceUser

@Database(entities = [User::class, Place::class, PlaceUser::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao
    abstract fun placeUserDao(): PlaceUserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Define migrations from version 1 to 2, and potentially from other versions as needed
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example migration: Add a new column 'newColumn' to User table
                database.execSQL("ALTER TABLE User ADD COLUMN newColumn TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add migrations here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
