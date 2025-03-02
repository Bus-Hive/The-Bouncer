package com.trackmybus.theBouncer.features.v1.domain.repository.google

import com.trackmybus.theBouncer.config.AppConfig
import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.errors.DataError
import com.trackmybus.theBouncer.features.v1.data.remote.mapper.DTOMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.remote.model.GoogleToken
import com.trackmybus.theBouncer.features.v1.data.remote.model.GoogleUser
import com.trackmybus.theBouncer.features.v1.data.remote.service.google.GoogleService

class GoogleRepositoryImpl(
    private val googleService: GoogleService,
    private val appConfig: AppConfig,
) : GoogleRepository {
    override suspend fun fetchGoogleTokens(code: String): Result<GoogleToken, DataError.Network> =
        googleService.fetchGoogleTokens(code).mapResult { it.toModel() }

    override suspend fun fetchGoogleUserInfo(accessToken: String): Result<GoogleUser, DataError.Network> =
        googleService.fetchGoogleUserInfo(accessToken).mapResult {
            it.toModel()
        }

    override fun getGoogleLoginUrl(): Result<String, DataError.Network> {
        try {
            val url =
                "https://accounts.google.com/o/oauth2/auth" +
                    "?client_id=${appConfig.googleConfig.clientId}" +
                    "&redirect_uri=${appConfig.googleConfig.redirectUri}" +
                    "&response_type=code" +
                    "&scope=email profile"
            return Result.Success(url)
        } catch (e: Exception) {
            return Result.Error(DataError.Network.Unknown)
        }
    }
}
