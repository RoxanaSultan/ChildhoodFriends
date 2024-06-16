package com.cst.cstacademy2024.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cst.cstacademy2024.models.Place

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place)

    @Query("SELECT * FROM places")
    suspend fun getAllPlaces(): List<Place>

//    @Query("SELECT * FROM places WHERE id = :id")
//    suspend fun getPlace(id: Int): Place?

    @Query("SELECT id FROM places WHERE name = :name AND location = :location")
    suspend fun getPlaceId(name: String, location: String): Int

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlaceById(id: Int): Place
}