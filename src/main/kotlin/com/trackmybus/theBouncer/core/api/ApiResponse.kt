package com.trackmybus.theBouncer.core.api

import kotlinx.serialization.Serializable

@Serializable
sealed class ApiResponse<out T> {
    @Serializable
    data class Success<out T>(
        val data: T,
        val message: String? = null,
        val code: Int? = null,
    ) : ApiResponse<T>()

    @Serializable
    data class Error(
        val code: Int,
        val message: String,
        val details: String? = null,
    ) : ApiResponse<Nothing>()
}
