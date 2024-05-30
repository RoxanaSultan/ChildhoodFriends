package com.cst.cstacademy2024.repositories

import com.cst.cstacademy2024.database.PlaceDao
import com.cst.cstacademy2024.models.Place

class PlaceRepository(private val placeDao: PlaceDao) {

        suspend fun insertPlace(place: Place) {
            placeDao.insertPlace(place)
        }

        suspend fun getPlace(id: Int): Place? {
            return placeDao.getPlace(id)
        }

        suspend fun getAllPlaces(): List<Place> {
            return placeDao.getAllPlaces()
        }

        // Add other repository methods as needed
}