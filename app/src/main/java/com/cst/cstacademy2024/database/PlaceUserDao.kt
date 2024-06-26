package com.cst.cstacademy2024.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cst.cstacademy2024.models.Place
import com.cst.cstacademy2024.models.PlaceUser
import com.cst.cstacademy2024.models.User

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

    @Query("SELECT placeId FROM places_users WHERE category = :category AND userId = :userId")
    suspend fun getPlacesByCategory(category:String, userId: Int): List<Int>

    @Query("DELETE FROM places_users WHERE placeId = :placeId AND userId = :userId")
    suspend fun deletePlaceUser(placeId: Int, userId: Int)

    @Query("DELETE FROM places_users WHERE userId != :userId")
    suspend fun deletePlacesAndUsers(userId: Int)

    @Query("SELECT category FROM places_users WHERE userId = :userId AND placeId = :placeId")
    suspend fun getCategoryByUserAndPlace(userId: Int, placeId: Int): String
}
