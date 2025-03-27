package app.bushive.theBouncer.features.v1.data.remote.service.google

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.errors.DataError
import app.bushive.theBouncer.features.v1.data.remote.dto.GoogleTokenDto
import app.bushive.theBouncer.features.v1.data.remote.dto.GoogleUserDto

interface GoogleService {
    suspend fun fetchGoogleTokens(code: String): Result<GoogleTokenDto, DataError.Network>

    suspend fun fetchGoogleUserInfo(accessToken: String): Result<GoogleUserDto, DataError.Network>
}
