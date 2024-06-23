package com.cst.cstacademy2024.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cst.cstacademy2024.models.Place
import com.cst.cstacademy2024.models.PlaceUser

@Dao
interface PlaceUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceUser(placeUser: PlaceUser)

    @Query("SELECT * FROM places_users WHERE placeId = :placeId AND userId = :userId")
    suspend fun getPlaceUser(placeId: Int, userId: Int): PlaceUser?

    @Query("SELECT * FROM places_users WHERE userId = :userId")
    suspend fun getPlaceUsersByUserId(userId: Int): List<PlaceUser>

    @Query("SELECT * FROM places_users WHERE placeId = :placeId")
    suspend fun getPlaceUsersByPlaceId(placeId: Int): List<PlaceUser>
    @Query("SELECT placeId FROM places_users WHERE userId = :userId")
    suspend fun getPlacesByUserId(userId: Int): List<Int>

    @Query("SELECT placeId FROM places_users WHERE category = :category")
    suspend fun getPlacesByCategory(category:String): List<Int>

    @Query("DELETE FROM places_users WHERE placeId = :placeId AND userId = :userId")
    suspend fun deletePlaceUser(placeId: Int, userId: Int)
}
