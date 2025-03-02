package com.trackmybus.theBouncer.features.v1.data.remote.model

data class GoogleToken(
    val accessToken: String?,
    val expiresIn: Int?,
    val idToken: String?,
    val scope: String?,
    val tokenType: String?,
)
