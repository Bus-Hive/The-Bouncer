package com.trackmybus.theKeg.core.api

import kotlinx.serialization.Serializable

@Serializable
sealed class ApiResponse<out T> {
    @Serializable
    data class Success<out T>(
        val data: T,
    ) : ApiResponse<T>()

    @Serializable
    data class Error(
        val errorCode: Int,
        val message: String,
        val details: String? = null,
    ) : ApiResponse<Nothing>()
}
