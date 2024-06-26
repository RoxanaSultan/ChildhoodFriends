package com.cst.cstacademy2024.repositories

import com.cst.cstacademy2024.database.PlaceUserDao
import com.cst.cstacademy2024.models.PlaceUser

class PlaceUserRepository(private val placeUserDao: PlaceUserDao) {

        suspend fun insertPlaceUser(placeUser: PlaceUser) {
            placeUserDao.insertPlaceUser(placeUser)
        }

//        suspend fun getPlaceUsers(placeId: Int): List<PlaceUser> {
//            return placeUserDao.getPlaceUsersByPlaceId(placeId)
//        }

        suspend fun getPlacesByUserId(userId: Int): List<Int> {
            return placeUserDao.getPlacesByUserId(userId)
        }

        suspend fun getPlaceUser(userId: Int, placeId: Int): PlaceUser? {
            return placeUserDao.getPlaceUser(userId, placeId)
        }
        suspend fun getPlacesByCategory(category: String, userId: Int): List<Int> {
            return placeUserDao.getPlacesByCategory(category, userId)
        }

    suspend fun deletePlaceUser(placeId: Int, userId: Int) {
        placeUserDao.deletePlaceUser(placeId, userId)
    }

    suspend fun deletePlacesAndUsers(userId: Int) {
        placeUserDao.deletePlacesAndUsers(userId)
    }

    suspend fun getCategoryByUserAndPlace(userId: Int, placeId: Int): String {
        return placeUserDao.getCategoryByUserAndPlace(userId, placeId)
    }

        // Add other repository methods as needed
}