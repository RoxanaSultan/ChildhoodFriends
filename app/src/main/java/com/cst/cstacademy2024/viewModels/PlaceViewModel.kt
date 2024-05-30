package com.cst.cstacademy2024.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.cst.cstacademy2024.database.AppDatabase
import com.cst.cstacademy2024.database.PlaceDao
import com.cst.cstacademy2024.models.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    private val placeDao: PlaceDao = AppDatabase.getDatabase(application).placeDao()

    fun insertPlace(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            placeDao.insertPlace(place)
        }
    }
    fun getAllPlaces(): LiveData<List<Place>> {
        return liveData(Dispatchers.IO) {
            emit(placeDao.getAllPlaces())
        }
    }
}
