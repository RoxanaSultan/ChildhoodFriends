package com.cst.cstacademy2024.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cst.cstacademy2024.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users INNER JOIN places_users ON users.id = places_users.userId INNER JOIN places ON places_users.placeId = places.id WHERE LOWER(places.name) LIKE LOWER(:place) AND places_users.category = :category AND userId != :userId")
    suspend fun getUsersByCategoryPlace(category: String, place: String, userId: Int): List<User>

    @Query("UPDATE users SET email = :email, first_name = :firstName, last_name = :lastName, phone = :phone WHERE id = :id")
    suspend fun updateUser(id: Int, email: String, firstName: String, lastName: String, phone: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE id != :userId")
    suspend fun deleteAllUsers(userId: Int)

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun userExists(username: String): Int
}