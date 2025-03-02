package com.trackmybus.theBouncer.features.v1.domain.usecase.google

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.DataError
import com.trackmybus.theBouncer.core.result.errors.ValidationError
import com.trackmybus.theBouncer.features.v1.data.local.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.local.model.User
import com.trackmybus.theBouncer.features.v1.data.local.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto
import com.trackmybus.theBouncer.features.v1.domain.repository.google.GoogleRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.jwt.JwtRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.user.UserRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.userPermissionGroup.UserPermissionGroupRepository
import io.ktor.util.logging.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime

class GoogleUseCaseImpl(
    private val logger: Logger,
    private val googleRepository: GoogleRepository,
    private val userRepository: UserRepository,
    private val jwtRepository: JwtRepository,
    private val permissionGroupRepository: PermissionGroupRepository,
    private val userPermissionGroupRepository: UserPermissionGroupRepository,
) : GoogleUseCase {
    override suspend fun login(): Result<String, RootError> = googleRepository.getGoogleLoginUrl()

    override suspend fun callback(code: String): Result<TokensDto, RootError> {
        val googleTokens =
            googleRepository.fetchGoogleTokens(code).onFailure {
                return Result.Error(it)
            }

        logger.info("Google tokens fetched successfully $googleTokens")

        val googleUser =
            googleRepository.fetchGoogleUserInfo(googleTokens.getDataOrNull()?.accessToken ?: "").onFailure {
                return Result.Error(it)
            }

        val userTransaction = userRepository.getByEmail(googleUser.getDataOrNull()?.email ?: "")
        logger.info("User transaction $userTransaction")
        var user: User? = userTransaction.getDataOrNull()
        when (userTransaction) {
            is Result.Error<*, *> -> {
                when (userTransaction.error) {
                    is DataError.Local.RecordNotFound -> {
                        val createUser =
                            createUser(
                                email = googleUser.getDataOrNull()?.email ?: "",
                                firstName = googleUser.getDataOrNull()?.givenName ?: "",
                                lastName = googleUser.getDataOrNull()?.familyName ?: "",
                            )
                        createUser.onFailure {
                            return Result.Error(it)
                        }

                        user =
                            createUser.getDataOrNull() ?: return Result.Error<TokensDto, RootError>(DataError.Local.Unknown)
                    }

                    else -> return Result.Error(userTransaction.error)
                }
            }

            is Result.Success<*, *> -> {
                logger.info("User already exists")
                if (user?.provider != AuthProvider.GOOGLE) {
                    return Result.Error(error = DataError.Network.Unauthorized, message = "Wrong auth provider")
                }
            }
        }

        val accessToken =
            jwtRepository.generateAccessToken(user).onFailure { return Result.Error(it) }.getDataOrNull()
                ?: return Result.Error(
                    ValidationError.UnknownError,
                )

        val refreshToken =
            jwtRepository.generateRefreshToken(user).onFailure { return Result.Error(it) }.getDataOrNull()
                ?: return Result.Error(
                    ValidationError.UnknownError,
                )

        return Result.Success(TokensDto(accessToken, refreshToken), message = "Successfully logged in")
    }

    override fun getGoogleLoginUrl(): Result<String, RootError> = googleRepository.getGoogleLoginUrl()

    private suspend fun createUser(
        email: String,
        firstName: String,
        lastName: String,
    ): Result<User, RootError> {
        val user =
            userRepository.add(
                User(
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    hashedPassword = "",
                    provider = AuthProvider.GOOGLE,
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                ),
            )

        val userUUID = user.getDataOrNull()?.id ?: return Result.Error(ValidationError.UnknownError)

        val basePermissionGroups =
            permissionGroupRepository
                .getBasePermissionGroups()
                .onFailure {
                    return Result.Error(it)
                }.getDataOrNull()
                .orEmpty()

        basePermissionGroups.forEach { permissionGroup ->
            userPermissionGroupRepository.add(
                UserPermissionGroup(
                    userId = userUUID,
                    permissionGroupId = permissionGroup.id,
                ),
            )
        }

        return user
    }
}
