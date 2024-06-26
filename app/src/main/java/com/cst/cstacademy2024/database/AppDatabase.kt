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
        var MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new table with CASCADE on delete
                database.execSQL("""
            CREATE TABLE new_places_users (
                placeId INTEGER NOT NULL,
                userId INTEGER NOT NULL,
                category TEXT NOT NULL,
                PRIMARY KEY(placeId, userId),
                FOREIGN KEY(placeId) REFERENCES places(id),
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
            )
        """)
                database.execSQL("INSERT INTO new_places_users SELECT * FROM places_users")
                database.execSQL("DROP TABLE places_users")
                database.execSQL("ALTER TABLE new_places_users RENAME TO places_users")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add the migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}