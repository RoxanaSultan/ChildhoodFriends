package com.cst.cstacademy2024.models

data class UserAPI(
    val id: Int,
    val email: String,
    val username: String,
    val password: String,
    val name: Name,
    val address: Address,
    val phone: String
)

data class Name(
    val firstname: String,
    val lastname: String
)

data class Address(
    val city: String,
    val street: String,
    val number: Int,
    val zipcode: String,
    val geolocation: GeoLocation
)

data class GeoLocation(
    val lat: String,
    val long: String
)