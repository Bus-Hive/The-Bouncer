package com.trackmybus.theBouncer.features.v1.domain.repository.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.IncorrectClaimException
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.MissingClaimException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.trackmybus.theBouncer.config.AppConfig
import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.JwtError
import com.trackmybus.theBouncer.features.v1.data.dao.session.SessionDao
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.model.Session
import com.trackmybus.theBouncer.features.v1.data.model.User
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto
import com.trackmybus.theBouncer.features.v1.domain.repository.permission.PermissionRepository
import io.ktor.util.logging.Logger
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import java.util.Date
import java.util.UUID

class JwtRepositoryImpl(
    private val logger: Logger,
    private val appConfig: AppConfig,
    private val sessionDao: SessionDao,
    private val userDao: UserDao,
    private val permissionRepository: PermissionRepository,
) : JwtRepository {
    override suspend fun generateAccessToken(user: User): Result<String, RootError> {
        try {
            val userId =
                user.id ?: return Result.Error(
                    JwtError.UserNotFound,
                    message = "User ID is missing or null",
                    data = null,
                )

            val permissions =
                permissionRepository
                    .getPermissionsByUserId(userId)
                    .onFailure {
                        logger.error("Error fetching permissions for user with ID: $userId")
                    }.getDataOrNull()
                    .orEmpty()

            val claims: Map<String, Any> =
                mapOf(
                    "userId" to user.id.toString(),
                    "email" to user.email,
                    "firstName" to user.firstName,
                    "lastName" to user.lastName,
                    "role" to user.role.name,
                    "permissions" to permissions.map { it.permission },
                    "type" to "access",
                )

            val token =
                JWT
                    .create()
                    .withAudience(appConfig.jwtConfig.audience)
                    .withIssuer(appConfig.jwtConfig.issuer)
                    .withClaims(claims)
                    .withExpiresAt(Date(System.currentTimeMillis() + appConfig.jwtConfig.accessTokenValiditySeconds * 1000))
                    .sign(Algorithm.HMAC256(appConfig.jwtConfig.secret))

            return Result.Success(data = token)
        } catch (e: JWTCreationException) {
            logger.error("JWT creation error: Invalid claims or other issues with token creation", e)
            return Result.Error(
                JwtError.InvalidClaims,
                message = "JWT creation error: Invalid claims or other issues",
                data = null,
            )
        } catch (e: IllegalArgumentException) {
            logger.error("JWT creation failed: Illegal arguments", e)
            return Result.Error(
                JwtError.InvalidArguments,
                message = "Illegal arguments provided for JWT creation",
                data = null,
            )
        } catch (e: OutOfMemoryError) {
            logger.error("JWT creation failed due to memory error", e)
            return Result.Error(JwtError.MemoryError, message = "Insufficient memory for JWT creation", data = null)
        } catch (e: Exception) {
            logger.error("Unexpected error during JWT creation", e)
            return Result.Error(
                JwtError.TokenGenerationFailed,
                message = "Unexpected error during token generation",
                data = null,
            )
        }
    }

    override suspend fun generateRefreshToken(user: User): Result<String, RootError> {
        try {
            val expiryTime = System.currentTimeMillis() + appConfig.jwtConfig.refreshTokenValiditySeconds * 1000
            val id = UUID.randomUUID()

            val token =
                JWT
                    .create()
                    .withAudience(appConfig.jwtConfig.audience)
                    .withIssuer(appConfig.jwtConfig.issuer)
                    .withJWTId(id.toString())
                    .withClaim("type", "refresh")
                    .withExpiresAt(Date(expiryTime))
                    .sign(Algorithm.HMAC256(appConfig.jwtConfig.secret))

            val session =
                Session(
                    sessionID = id,
                    userId = user.id,
                    refreshToken = token,
                    expiresAt = Instant.fromEpochMilliseconds(expiryTime).toLocalDateTime(UTC),
                )

            val newSession = sessionDao.addSession(session)
            newSession.onFailure {
                logger.error("Error adding session to database")
                return Result.Error(it)
            }

            return Result.Success(data = token, message = "Refresh token generated successfully")
        } catch (e: JWTCreationException) {
            logger.error("JWT creation error: Invalid claims or other issues with token creation", e)
            return Result.Error(
                JwtError.InvalidClaims,
                message = "JWT creation error: Invalid claims or other issues",
                data = null,
            )
        } catch (e: IllegalArgumentException) {
            logger.error("JWT creation failed: Illegal arguments", e)
            return Result.Error(
                JwtError.InvalidArguments,
                message = "Illegal arguments provided for JWT creation",
                data = null,
            )
        } catch (e: OutOfMemoryError) {
            logger.error("JWT creation failed due to memory error", e)
            return Result.Error(JwtError.MemoryError, message = "Insufficient memory for JWT creation", data = null)
        } catch (e: Exception) {
            logger.error("Unexpected error during JWT creation", e)
            return Result.Error(
                JwtError.TokenGenerationFailed,
                message = "Unexpected error during token generation",
                data = null,
            )
        }
    }

    override suspend fun validateAccessToken(accessToken: String): Result<Boolean, RootError> {
        try {
            if (accessToken.isEmpty()) {
                return Result.Error(JwtError.MissingToken, message = "Access token is empty")
            }

            JWT
                .require(Algorithm.HMAC256(appConfig.jwtConfig.secret))
                .withAudience(appConfig.jwtConfig.audience)
                .withIssuer(appConfig.jwtConfig.issuer)
                .withClaim("type", "access")
                .build()
                .verify(accessToken)

            val decodedJWT = JWT.decode(accessToken)
            if (decodedJWT.getClaim("type").asString() != "access") {
                return Result.Error(JwtError.InvalidTokenType, message = "Invalid token type")
            }

            return Result.Success(true)
        } catch (e: AlgorithmMismatchException) {
            logger.error("Algorithm mismatch error during access token validation", e)
            return Result.Error(JwtError.InvalidToken, message = "Algorithm mismatch error")
        } catch (e: SignatureVerificationException) {
            logger.error("Signature verification error during access token validation", e)
            return Result.Error(JwtError.InvalidToken, message = "Signature verification error")
        } catch (e: TokenExpiredException) {
            logger.error("Token expired error during access token validation", e)
            return Result.Error(JwtError.TokenExpired, message = "Token has expired")
        } catch (e: MissingClaimException) {
            logger.error("Missing claim error during access token validation", e)
            return Result.Error(JwtError.InvalidToken, message = "Missing claim error")
        } catch (e: IncorrectClaimException) {
            logger.error("Incorrect claim error during access token validation", e)
            return Result.Error(JwtError.InvalidToken, message = "Incorrect claim error")
        } catch (e: Exception) {
            logger.error("Error validating access token", e)
            return Result.Error(JwtError.InvalidToken)
        }
    }

    override suspend fun validateRefreshToken(refreshToken: String): Result<Boolean, RootError> {
        try {
            if (refreshToken.isEmpty()) {
                return Result.Error(JwtError.MissingToken, message = "Refresh token is empty")
            }

            JWT
                .require(Algorithm.HMAC256(appConfig.jwtConfig.secret))
                .withAudience(appConfig.jwtConfig.audience)
                .withIssuer(appConfig.jwtConfig.issuer)
                .withClaim("type", "refresh")
                .build()
                .verify(refreshToken)

            val decodedJWT = JWT.decode(refreshToken)
            if (decodedJWT.getClaim("type").asString() != "refresh") {
                return Result.Error(JwtError.InvalidTokenType, message = "Invalid token type")
            }

            return Result.Success(true)
        } catch (e: AlgorithmMismatchException) {
            logger.error("Algorithm mismatch error during access token validation", e)
            return Result.Error(JwtError.InvalidToken, message = "Algorithm mismatch error")
        } catch (e: SignatureVerificationException) {
            logger.error("Signature verification error during access token validation", e)
            return Result.Error(JwtError.InvalidToken, message = "Signature verification error")
        } catch (e: TokenExpiredException) {
            logger.error("Token expired error during access token validation", e)
            return Result.Error(JwtError.TokenExpired, message = "Token has expired")
        } catch (e: MissingClaimException) {
            logger.error("Missing claim error during access token validation", e)
            return Result.Error(JwtError.InvalidToken, message = "Missing claim error")
        } catch (e: IncorrectClaimException) {
            logger.error("Incorrect claim error during access token validation", e)
            return Result.Error(JwtError.InvalidToken, message = "Incorrect claim error")
        } catch (e: JWTDecodeException) {
            logger.error("Error decoding refresh token", e)
            return Result.Error(JwtError.TokenDecodingFailed, message = "Error decoding refresh token")
        } catch (e: Exception) {
            logger.error("Error validating access token", e)
            return Result.Error(JwtError.InvalidToken)
        }
    }

    override suspend fun refreshAccessToken(refreshToken: String): Result<TokensDto, RootError> {
        try {
            val validRefreshToken =
                validateRefreshToken(refreshToken)
                    .onFailure {
                        return Result.Error(it)
                    }.getDataOrNull() ?: return Result.Error(JwtError.InvalidToken, message = "Invalid refresh token")

            if (validRefreshToken != true) {
                return Result.Error(JwtError.InvalidToken, message = "Invalid refresh token")
            }

            val decodedJWT = JWT.decode(refreshToken)
            val tokenId = decodedJWT.id

            val session =
                sessionDao
                    .getSessionById(UUID.fromString(tokenId))
                    .onFailure { return Result.Error(it) }
                    .getDataOrNull() ?: return Result.Error(JwtError.SessionNotFound, message = "Session not found")

            val userId = session.userId ?: return Result.Error(JwtError.UserNotFound, message = "User not found")

            val user =
                userDao
                    .getUserById(userId)
                    .onFailure {
                        return Result.Error(it)
                    }.getDataOrNull() ?: return Result.Error(JwtError.UserNotFound, message = "User not found")

            val accessToken =
                generateAccessToken(user)
                    .onFailure {
                        return Result.Error(it)
                    }.getDataOrNull() ?: return Result.Error(
                    JwtError.TokenGenerationFailed,
                    message = "Error generating access token",
                )

            val newRefreshToken =
                generateRefreshToken(user)
                    .onFailure {
                        return Result.Error(it)
                    }.getDataOrNull() ?: return Result.Error(
                    JwtError.TokenGenerationFailed,
                    message = "Error generating refresh token",
                )

            return Result.Success(TokensDto(accessToken = accessToken, refreshToken = newRefreshToken))
        } catch (e: JWTDecodeException) {
            logger.error("Error decoding refresh token", e)
            return Result.Error(JwtError.TokenDecodingFailed, message = "Error decoding refresh token")
        } catch (e: Exception) {
            logger.error("Error refreshing access token", e)
            return Result.Error(JwtError.TokenRefreshFailed, message = "Error refreshing access token")
        }
    }

    override suspend fun revokeSession(refreshToken: String): Result<Unit, RootError> {
        try {
            val validRefreshToken =
                validateRefreshToken(refreshToken)
                    .onFailure {
                        return Result.Error(it)
                    }.getDataOrNull()
            if (validRefreshToken != true) {
                return Result.Error(JwtError.InvalidToken, message = "Invalid refresh token")
            }

            val decodedJWT = JWT.decode(refreshToken)
            val tokenId = decodedJWT.id

            sessionDao.deleteSession(UUID.fromString(tokenId)).onFailure {
                return Result.Error(it)
            }

            return Result.Success(Unit)
        } catch (e: JWTDecodeException) {
            logger.error("Error decoding refresh token", e)
            return Result.Error(JwtError.TokenDecodingFailed, message = "Error decoding refresh token")
        } catch (e: Exception) {
            logger.error("Error refreshing access token", e)
            return Result.Error(JwtError.TokenRefreshFailed, message = "Error refreshing access token")
        }
    }

    fun JWTCreator.Builder.withClaims(claims: Map<String, Any>): JWTCreator.Builder {
        claims.forEach { (key, value) ->
            when (value) {
                is String -> this.withClaim(key, value)
                is Int -> this.withClaim(key, value)
                is Long -> this.withClaim(key, value)
                is Double -> this.withClaim(key, value)
                is Boolean -> this.withClaim(key, value)
                is Date -> this.withClaim(key, value)
                is List<*> -> this.withArrayClaim(key, value.map { it.toString() }.toTypedArray())
                else -> throw IllegalArgumentException("Unsupported claim type: ${value::class.java}")
            }
        }
        return this
    }
}
