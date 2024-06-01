package com.cst.cstacademy2024.repositories

import com.cst.cstacademy2024.database.UserDao
import com.cst.cstacademy2024.models.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUser(username: String, password: String): User? {
        return userDao.getUser(username, password)
    }

    suspend fun checkUserExists(username: String, password: String): Boolean {
        val user = userDao.getUser(username, password)
        return user != null
    }

    suspend fun getUsersByCategoryPlace(category: String, place: String): List<User> {
        return userDao.getUsersByCategoryPlace(category, place)
    }
    // Add other repository methods as needed
}