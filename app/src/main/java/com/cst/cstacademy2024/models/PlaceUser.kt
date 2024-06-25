package com.cst.cstacademy2024.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "places_users",
    primaryKeys = ["placeId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["placeId"]
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Example of cascading deletion
        )
    ],
    indices = [Index(value = ["placeId"]), Index(value = ["userId"])]
)
data class PlaceUser(
    val placeId: Int,
    val userId: Int,
    val category: String? // Nullable String field
)
