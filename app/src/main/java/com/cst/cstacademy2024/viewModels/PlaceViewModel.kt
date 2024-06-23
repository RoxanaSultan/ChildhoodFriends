package com.cst.cstacademy2024.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.cst.cstacademy2024.database.AppDatabase
import com.cst.cstacademy2024.database.PlaceDao
import com.cst.cstacademy2024.models.Place
import com.cst.cstacademy2024.repositories.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    //    private val placeDao: PlaceDao = AppDatabase.getDatabase(application).placeDao()
    private val placeRepository: PlaceRepository

    init {
        val placeDao = AppDatabase.getDatabase(application).placeDao()
        placeRepository = PlaceRepository(placeDao)
    }

    fun insertPlace(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.insertPlace(place)
        }
    }
    fun getAllPlaces(): LiveData<List<Place>> {
        return liveData(Dispatchers.IO) {
            emit(placeRepository.getAllPlaces())
        }
    }

    fun getPlace(name: String, location: String): LiveData<Int> {
        return liveData(Dispatchers.IO) {
            emit(placeRepository.getPlace(name, location))
        }
    }

    fun getPlaceById(id: Int): LiveData<Place> {
        return liveData(Dispatchers.IO) {
            emit(placeRepository.getPlaceById(id))
        }
    }

    fun deletePlace(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.deletePlace(id)
        }
    }
}