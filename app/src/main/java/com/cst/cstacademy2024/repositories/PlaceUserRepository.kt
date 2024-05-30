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

        // Add other repository methods as needed
}