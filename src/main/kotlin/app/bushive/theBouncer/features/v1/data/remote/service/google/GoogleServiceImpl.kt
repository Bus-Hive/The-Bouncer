package app.bushive.theBouncer.features.v1.data.remote.service.google

import app.bushive.theBouncer.config.AppConfig
import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.errors.DataError
import app.bushive.theBouncer.features.v1.data.remote.dto.GoogleTokenDto
import app.bushive.theBouncer.features.v1.data.remote.dto.GoogleUserDto
import app.bushive.theBouncer.features.v1.data.remote.service.apiClient.ApiClientService
import io.ktor.http.HttpHeaders
import io.ktor.util.logging.Logger

class GoogleServiceImpl(
    private val logger: Logger,
    private val apiClientService: ApiClientService,
    private val appConfig: AppConfig,
) : GoogleService {
    override suspend fun fetchGoogleTokens(code: String): Result<GoogleTokenDto, DataError.Network> =
        apiClientService.post(
            path = "https://oauth2.googleapis.com/token",
            body =
                mapOf(
                    "code" to code,
                    "client_id" to appConfig.googleConfig.clientId,
                    "client_secret" to appConfig.googleConfig.clientSecret,
                    "redirect_uri" to appConfig.googleConfig.redirectUri,
                    "grant_type" to appConfig.googleConfig.grantType,
                ),
        )

    override suspend fun fetchGoogleUserInfo(accessToken: String): Result<GoogleUserDto, DataError.Network> =
        apiClientService.get(
            path = "https://www.googleapis.com/oauth2/v2/userinfo",
            header = mapOf(HttpHeaders.Authorization to "Bearer $accessToken"),
        )
}
