package com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.HashError
import com.trackmybus.theBouncer.core.result.errors.ValidationError
import com.trackmybus.theBouncer.features.v1.data.local.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.local.model.User
import com.trackmybus.theBouncer.features.v1.data.local.model.UserPermissionGroup
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
    ): Result<Unit, RootError> {
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            logger.error("Empty field")
            return Result.Error(error = ValidationError.EmptyField, message = "Empty field")
        }

        if (!password.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$"))) {
            logger.error("Invalid password")
            return Result.Error(error = ValidationError.InvalidCredentials, message = "Invalid password")
        }

        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))) {
            logger.error("Invalid email")
            return Result.Error(error = ValidationError.InvalidEmail, message = "Invalid email")
        }

        val isEmailUnique =
            userRepository
                .isEmailUnique(email)
                .onFailure {
                    return Result.Error(it)
                }.getDataOrNull()
        if (isEmailUnique != true) {
            return Result.Error(error = ValidationError.EmailAlreadyExists, message = "Email already exists")
        }

        val hashedPassword =
            passwordHashRepository.hashPassword(password).onFailure { return Result.Error(it) }.getDataOrNull()
                ?: return Result.Error(
                    HashError.HashGenerationFailed,
                )

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
            userRepository
                .add(user)
                .onFailure { return Result.Error(it) }
                .getDataOrNull()
                ?.id ?: return Result.Error(
                ValidationError.UnknownError,
            )

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

        return Result.Success(Unit)
    }

    override suspend fun loginUser(
        email: String,
        password: String,
    ): Result<TokensDto, RootError> {
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(error = ValidationError.EmptyField, message = "Empty field")
        }

        if (!password.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$"))) {
            return Result.Error(error = ValidationError.InvalidCredentials, message = "Invalid password")
        }

        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))) {
            return Result.Error(error = ValidationError.InvalidEmail, message = "Invalid email")
        }

        val user =
            userRepository.getByEmail(email).onFailure { return Result.Error(it) }.getDataOrNull()
                ?: return Result.Error(ValidationError.UnknownError)

        val isPasswordValid =
            passwordHashRepository
                .verifyPassword(password, user.hashedPassword)
                .onFailure { return Result.Error(it) }
                .getDataOrNull()
                ?: return Result.Error(HashError.HashVerificationFailed)
        if (!isPasswordValid) {
            return Result.Error(error = ValidationError.InvalidCredentials, message = "Invalid credentials")
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
}
