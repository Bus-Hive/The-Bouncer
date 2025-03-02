package com.trackmybus.theBouncer.features.v1.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenDto(
    @SerialName("access_token")
    val accessToken: String?,
    @SerialName("expires_in")
    val expiresIn: Int?,
    @SerialName("id_token")
    val idToken: String?,
    @SerialName("scope")
    val scope: String?,
    @SerialName("token_type")
    val tokenType: String?
)