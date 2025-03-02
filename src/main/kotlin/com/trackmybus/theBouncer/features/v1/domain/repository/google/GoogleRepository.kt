package com.trackmybus.theBouncer.features.v1.domain.repository.google

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.errors.DataError
import com.trackmybus.theBouncer.features.v1.data.remote.model.GoogleToken
import com.trackmybus.theBouncer.features.v1.data.remote.model.GoogleUser

interface GoogleRepository {
    suspend fun fetchGoogleTokens(code: String): Result<GoogleToken, DataError.Network>

    suspend fun fetchGoogleUserInfo(accessToken: String): Result<GoogleUser, DataError.Network>

    fun getGoogleLoginUrl(): Result<String, DataError.Network>
}
