package com.cst.cstacademy2024.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.cst.cstacademy2024.database.AppDatabase
import com.cst.cstacademy2024.database.UserDao
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.models.UserAPI
import com.cst.cstacademy2024.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val _userCount = MutableLiveData<Int>()
    val userCount: LiveData<Int> get() = _userCount

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

    fun getUserByUsername(username: String): LiveData<User?> {
        return liveData(Dispatchers.IO) {
            emit(userRepository.getUserByUsername(username))
        }
    }

    fun getUsersByCategoryPlace(category: String, place: String, userId: Int): LiveData<List<User>> {
        return liveData(Dispatchers.IO) {
            emit(userRepository.getUsersByCategoryPlace(category, place, userId))
        }
    }

    fun updateUser(id: Int, email: String, firstName: String, lastName: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUser(id, email, firstName, lastName, phone)
        }
    }

    fun deleteUsers(users: List<UserAPI>) {
        viewModelScope.launch(Dispatchers.IO) {
            for (user in users) {
                userRepository.deleteUser(userRepository.getUser(user.username, user.password)!!.id)
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteUser(userId)
        }
    }

    fun addUser(userApi: UserAPI) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = User(username = userApi.username, password = userApi.password, firstName = userApi.name.firstname, lastName = userApi.name.lastname, email = userApi.email, phone = userApi.phone)
            userRepository.insertUser(user)
        }
    }

    fun getUserById(userId: Int): LiveData<User> {
        return liveData(Dispatchers.IO) {
            emit(userRepository.getUserById(userId))
        }
    }

    suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }

    suspend fun getUserSync(username: String, password: String): User? {
        var user: User? = null
        viewModelScope.launch(Dispatchers.IO) {
            user = userRepository.getUser(username, password)
        }.join()  // Wait for the coroutine to finish
        return user
    }

    fun getUserCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = userRepository.getAllUsers().size
            _userCount.postValue(count) // Update the MutableLiveData
        }
    }

    fun deleteAllUsers(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteAllUsers(userId)
        }
    }

}
