package com.cst.cstacademy2024

import com.cst.cstacademy2024.models.UserAPI
import retrofit2.http.GET

interface FakeApiService {
    @GET("users")
    suspend fun getUsers(): List<UserAPI>
}