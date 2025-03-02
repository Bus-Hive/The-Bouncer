package com.trackmybus.theBouncer.features.v1.data.remote.service.google

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.errors.DataError
import com.trackmybus.theBouncer.features.v1.data.remote.dto.GoogleTokenDto
import com.trackmybus.theBouncer.features.v1.data.remote.dto.GoogleUserDto

interface GoogleService {
    suspend fun fetchGoogleTokens(code: String): Result<GoogleTokenDto, DataError.Network>

    suspend fun fetchGoogleUserInfo(accessToken: String): Result<GoogleUserDto, DataError.Network>
}
