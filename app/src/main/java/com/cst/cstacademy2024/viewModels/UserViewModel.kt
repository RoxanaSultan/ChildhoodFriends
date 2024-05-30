package com.cst.cstacademy2024.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.cst.cstacademy2024.database.AppDatabase
import com.cst.cstacademy2024.database.UserDao
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
    }

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insertUser(user)
        }
    }

    fun getUser(username: String, password: String): LiveData<User?> {
        return liveData(Dispatchers.IO) {
            emit(userRepository.getUser(username, password))
        }
    }

}
