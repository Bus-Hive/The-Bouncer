package com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword

import com.trackmybus.theBouncer.features.v1.data.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.model.User
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto
import com.trackmybus.theBouncer.features.v1.domain.repository.jwt.JwtRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.passowrdHash.PasswordHashRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.user.UserRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.userPermissionGroup.UserPermissionGroupRepository
import io.ktor.util.logging.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime

class EmailPasswordUseCaseImpl(
    private val logger: Logger,
    private val userRepository: UserRepository,
    private val passwordHashRepository: PasswordHashRepository,
    private val jwtRepository: JwtRepository,
    private val permissionGroupRepository: PermissionGroupRepository,
    private val userPermissionGroupRepository: UserPermissionGroupRepository,
) : EmailPasswordUseCase {
    override suspend fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
    ): Result<Unit> {
        val isEmailUnique = userRepository.isEmailUnique(email).getOrNull()
        if (isEmailUnique != true) {
            return Result.failure(Exception("Email $email is not unique"))
        }

        val hashedPassword = passwordHashRepository.hashPassword(password)

        val user =
            User(
                email = email,
                hashedPassword = hashedPassword,
                firstName = firstName,
                lastName = lastName,
                provider = AuthProvider.EMAIL_PASSWORD,
                createdAt = Clock.System.now().toLocalDateTime(UTC),
            )

        val userUUID =
            userRepository.add(user).getOrNull()
                ?: return Result.failure(Exception("Error adding user"))

        val basePermissionGroups = permissionGroupRepository.getBasePermissionGroups()

        if (basePermissionGroups.isFailure) {
            return Result.failure(Exception("Error getting base permission groups"))
        }

        basePermissionGroups.getOrNull()?.forEach { permissionGroup ->
            userPermissionGroupRepository.add(
                UserPermissionGroup(
                    userId = userUUID,
                    permissionGroupId = permissionGroup.id,
                ),
            )
        }

        return Result.success(Unit)
    }

    override suspend fun loginUser(
        email: String,
        password: String,
    ): Result<TokensDto> {
        val user =
            userRepository.getByEmail(email).getOrNull()
                ?: return Result.failure(Exception("User with email $email not found"))

        val isPasswordValid = passwordHashRepository.verifyPassword(password, user.hashedPassword)
        if (!isPasswordValid) {
            return Result.failure(Exception("Invalid password"))
        }

        val accessToken =
            jwtRepository.generateAccessToken(user).getOrNull()
                ?: return Result.failure(Exception("Error generating access token"))

        val refreshToken =
            jwtRepository.generateRefreshToken(user).getOrNull()
                ?: return Result.failure(Exception("Error generating refresh token"))

        return Result.success(TokensDto(accessToken, refreshToken))
    }
}
