package com.cst.cstacademy2024.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.cst.cstacademy2024.database.AppDatabase
import com.cst.cstacademy2024.database.PlaceUserDao
import com.cst.cstacademy2024.models.PlaceUser
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.models.UserAPI
import com.cst.cstacademy2024.repositories.PlaceUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceUserViewModel(application: Application) : AndroidViewModel(application) {

    //private val placeUserDao: PlaceUserDao = AppDatabase.getDatabase(application).placeUserDao()

    private val placeUserRepository: PlaceUserRepository
    init {
        val placeUserDao = AppDatabase.getDatabase(application).placeUserDao()
        placeUserRepository = PlaceUserRepository(placeUserDao)
    }
    fun insertPlaceUser(placeUser: PlaceUser) {
        viewModelScope.launch(Dispatchers.IO) {
            placeUserRepository.insertPlaceUser(placeUser)
        }
    }

    fun getPlacesByUserId(userId: Int): LiveData<List<Int>> {
        return liveData(Dispatchers.IO) {
            emit(placeUserRepository.getPlacesByUserId(userId))
        }
    }

    fun getPlacesByCategory(category: String): LiveData<List<Int>> {
        return liveData(Dispatchers.IO) {
            emit(placeUserRepository.getPlacesByCategory(category))
        }
    }

    fun deletePlaceUser(placeId: Int, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            placeUserRepository.deletePlaceUser(placeId, userId)
        }
    }

    fun deletePlacesAndUsers(users: List<UserAPI>) {
        viewModelScope.launch(Dispatchers.IO) {
            for (user in users) {
                placeUserRepository.deletePlacesAndUsers(user.id)
            }
        }
    }

    fun getCategoryByUserAndPlace(userId: Int, placeId: Int): LiveData<String> {
        return liveData(Dispatchers.IO) {
            emit(placeUserRepository.getCategoryByUserAndPlace(userId, placeId))
        }
    }
}