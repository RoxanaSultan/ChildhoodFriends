package com.cst.cstacademy2024.repositories

import com.cst.cstacademy2024.database.PlaceDao
import com.cst.cstacademy2024.models.Place

class PlaceRepository(private val placeDao: PlaceDao) {

    suspend fun insertPlace(place: Place) {
        placeDao.insertPlace(place)
    }

    suspend fun getPlace(name: String, location: String): Int {
        return placeDao.getPlaceId(name, location)
    }

    suspend fun getAllPlaces(): List<Place> {
        return placeDao.getAllPlaces()
    }

    suspend fun getPlaceById(id: Int, userId: Int): Place {
        return placeDao.getPlaceById(id, userId)
    }

    suspend fun deletePlace(id: Int) {
        placeDao.deletePlace(id)
    }

}