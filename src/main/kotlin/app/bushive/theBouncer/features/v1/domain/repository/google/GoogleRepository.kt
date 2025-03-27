package app.bushive.theBouncer.features.v1.domain.repository.google

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.errors.DataError
import app.bushive.theBouncer.features.v1.data.remote.model.GoogleToken
import app.bushive.theBouncer.features.v1.data.remote.model.GoogleUser

interface GoogleRepository {
    suspend fun fetchGoogleTokens(code: String): Result<GoogleToken, DataError.Network>

    suspend fun fetchGoogleUserInfo(accessToken: String): Result<GoogleUser, DataError.Network>

    fun getGoogleLoginUrl(): Result<String, DataError.Network>
}
