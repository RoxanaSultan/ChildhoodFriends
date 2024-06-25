package com.cst.cstacademy2024.repositories

import androidx.lifecycle.LiveData
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

    suspend fun updateUser(id: Int, email: String, firstName: String, lastName: String, phone: String) {
        userDao.updateUser(id, email, firstName, lastName, phone)
    }

    suspend fun deleteUser(userId: Int) {
        userDao.deleteUser(userId)
    }

    suspend fun addUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserById(userId: Int): User {
        return userDao.getUserById(userId)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    } 

}