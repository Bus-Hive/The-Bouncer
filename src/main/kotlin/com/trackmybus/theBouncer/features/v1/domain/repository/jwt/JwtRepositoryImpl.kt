package com.trackmybus.theBouncer.features.v1.domain.repository.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.trackmybus.theBouncer.config.AppConfig
import com.trackmybus.theBouncer.features.v1.data.dao.session.SessionDao
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.mapper.UserEntityMapper.toModel
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
    override suspend fun generateAccessToken(user: User): Result<String> =
        runCatching {
            val permissions =
                permissionRepository.getPermissionsByUserId(user.id ?: throw Exception("User ID is null")).getOrNull()
                    ?: emptyList()
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

            token
        }.onFailure {
            logger.error("Error generating access token", it)
            Exception("Error generating access token")
        }

    override suspend fun generateRefreshToken(user: User): Result<String> =
        runCatching {
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
                    expiresAt =
                        Instant
                            .fromEpochMilliseconds(expiryTime)
                            .toLocalDateTime(UTC),
                )
            sessionDao.addSession(session)

            token
        }.onFailure {
            logger.error("Error generating refresh token", it)
            Exception("Error generating refresh token")
        }

    override suspend fun validateAccessToken(accessToken: String): Result<Boolean> =
        runCatching {
            JWT
                .require(Algorithm.HMAC256(appConfig.jwtConfig.secret))
                .withAudience(appConfig.jwtConfig.audience)
                .withIssuer(appConfig.jwtConfig.issuer)
                .withClaim("type", "access")
                .build()
                .verify(accessToken)

            val decodedJWT = JWT.decode(accessToken)
            if (decodedJWT.getClaim("type").asString() != "access") {
                throw Exception("Invalid token type")
            }
            true
        }.onFailure {
            logger.error("Error validating access token", it)
            false
        }

    override suspend fun validateRefreshToken(refreshToken: String): Result<Boolean> =
        runCatching {
            JWT
                .require(Algorithm.HMAC256(appConfig.jwtConfig.secret))
                .withAudience(appConfig.jwtConfig.audience)
                .withIssuer(appConfig.jwtConfig.issuer)
                .withClaim("type", "refresh")
                .build()
                .verify(refreshToken)

            val decodedJWT = JWT.decode(refreshToken)
            if (decodedJWT.getClaim("type").asString() != "refresh") {
                throw Exception("Invalid token type")
            }

            true
        }.onFailure {
            logger.error("Error validating refresh token", it)
            false
        }

    override suspend fun refreshAccessToken(refreshToken: String): Result<TokensDto> =
        runCatching {
            val validRefreshToken = validateRefreshToken(refreshToken).getOrNull()
            if (validRefreshToken != true) {
                Exception("Invalid refresh token")
            }

            val decodedJWT = JWT.decode(refreshToken)
            val tokenId = decodedJWT.id

            val session = sessionDao.getSessionById(UUID.fromString(tokenId))
            if (session.isFailure || session.getOrNull() == null) {
                Exception("Error getting session by id: $tokenId")
            }
            val userId = session.getOrNull()?.userId?.value
            require(userId != null) {
                "User ID cannot be null"
            }

            val user = userDao.getUserById(userId).getOrNull()?.toModel()

            val accessToken =
                generateAccessToken(
                    user ?: throw Exception("Error getting user"),
                ).getOrNull()

            val newRefreshToken = generateRefreshToken(user).getOrNull()

            TokensDto(
                accessToken = accessToken ?: throw Exception("Error generating access token"),
                refreshToken = newRefreshToken ?: throw Exception("Error generating refresh token"),
            )
        }

    override suspend fun revokeSession(refreshToken: String): Result<Unit> {
        val validRefreshToken = validateRefreshToken(refreshToken).getOrNull()
        if (validRefreshToken != true) {
            return Result.failure(Exception("Invalid refresh token"))
        }

        val decodedJWT = JWT.decode(refreshToken)
        val tokenId = decodedJWT.id

        val session = sessionDao.getSessionById(UUID.fromString(tokenId))
        if (session.isFailure || session.getOrNull() == null) {
            return Result.failure(Exception("Error getting session by id: $tokenId"))
        }

        sessionDao.deleteSession(UUID.fromString(tokenId))
        return Result.success(Unit)
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
