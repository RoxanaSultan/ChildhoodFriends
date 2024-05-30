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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceUserViewModel(application: Application) : AndroidViewModel(application) {

    private val placeUserDao: PlaceUserDao = AppDatabase.getDatabase(application).placeUserDao()

    fun insertPlaceUser(placeUser: PlaceUser) {
        viewModelScope.launch(Dispatchers.IO) {
            placeUserDao.insertPlaceUser(placeUser)
        }
    }

    fun getPlacesByUserId(userId: Int): LiveData<List<Int>> {
        return liveData(Dispatchers.IO) {
            emit(placeUserDao.getPlacesByUserId(userId))
        }
    }
}
