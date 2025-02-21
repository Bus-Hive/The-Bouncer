package com.trackmybus.theBouncer.features.v1.resource

import com.trackmybus.theBouncer.config.TheBouncer
import com.trackmybus.theBouncer.core.api.respondError
import com.trackmybus.theBouncer.core.api.respondSuccess
import com.trackmybus.theBouncer.features.v1.domain.dto.AccessTokenDto
import com.trackmybus.theBouncer.features.v1.domain.dto.RefreshTokenDto
import com.trackmybus.theBouncer.features.v1.domain.usecase.token.TokenUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

@Resource("/token")
class TokenPublic(
    val parent: TheBouncer.V1.Public,
) {
    @Resource("/refresh")
    class Refresh(
        val parent: TokenPublic,
    )
}

@Resource("/token")
class TokenProtected(
    val parent: TheBouncer.V1.Protected,
) {
    @Resource("/validate")
    class Validate(
        val parent: TokenProtected,
    ) {
        @Resource("/access")
        class Access(
            val parent: Validate,
        )

        @Resource("/refresh")
        class Refresh(
            val parent: Validate,
        )
    }

    @Resource("/revoke")
    class Revoke(
        val parent: TokenProtected,
    )
}

fun Application.tokenRoutes() {
    routing {
        refreshTokenRoute()
        validateAccessTokenRoute()
        validateRefreshTokenRoute()
        revokeSessionRoute()
    }
}

fun Route.refreshTokenRoute() {
    post<TokenPublic.Refresh, RefreshTokenDto> { resource, body ->
        val tokenUseCase: TokenUseCase by application.inject()
        runCatching {
            tokenUseCase.refreshToken(body.refreshToken)
        }.onSuccess {
            call.respondSuccess(it.getOrNull())
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to refresh token",
                details = it.message,
            )
        }
    }
}

fun Route.validateAccessTokenRoute() {
    post<TokenProtected.Validate.Access, AccessTokenDto> { resource, body ->
        val tokenUseCase: TokenUseCase by application.inject()
        runCatching {
            tokenUseCase.validateAccessToken(body.accessToken)
        }.onSuccess {
            call.respondSuccess(it.getOrNull())
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to validate access token",
                details = it.message,
            )
        }
    }
}

fun Route.validateRefreshTokenRoute() {
    post<TokenProtected.Validate.Refresh, RefreshTokenDto> { resource, body ->
        val tokenUseCase: TokenUseCase by application.inject()
        runCatching {
            tokenUseCase.validateRefreshToken(body.refreshToken)
        }.onSuccess {
            call.respondSuccess(it.getOrNull())
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to validate refresh token",
                details = it.message,
            )
        }
    }
}

fun Route.revokeSessionRoute() {
    post<TokenProtected.Revoke, RefreshTokenDto> { resource, body ->
        val tokenUseCase: TokenUseCase by application.inject()
        runCatching {
            tokenUseCase.revokeSession(body.refreshToken)
        }.onSuccess {
            call.respondSuccess(it.getOrNull())
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to revoke session",
                details = it.message,
            )
        }
    }
}
