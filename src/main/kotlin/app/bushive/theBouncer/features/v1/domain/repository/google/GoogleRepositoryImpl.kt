package app.bushive.theBouncer.features.v1.domain.repository.google

import app.bushive.theBouncer.config.AppConfig
import app.bushive.theBouncer.core.mapper.ResultMapper.mapResult
import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.errors.DataError
import app.bushive.theBouncer.features.v1.data.remote.mapper.DTOMapper.toModel
import app.bushive.theBouncer.features.v1.data.remote.model.GoogleToken
import app.bushive.theBouncer.features.v1.data.remote.model.GoogleUser
import app.bushive.theBouncer.features.v1.data.remote.service.google.GoogleService

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
