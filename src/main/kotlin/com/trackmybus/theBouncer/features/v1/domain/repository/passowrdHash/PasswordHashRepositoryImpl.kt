package com.trackmybus.theBouncer.features.v1.domain.repository.passowrdHash

import com.trackmybus.theBouncer.config.AppConfig
import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.HashError
import io.ktor.util.logging.Logger
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.security.SecureRandom
import java.util.Base64

class PasswordHashRepositoryImpl(
    private val logger: Logger,
    private val appConfig: AppConfig,
) : PasswordHashRepository {
    override fun hashPassword(password: String): Result<String, RootError> {
        try {
            val salt =
                ByteArray(appConfig.passwordHashConfig.saltLength).apply {
                    SecureRandom().nextBytes(this)
                }

            val params =
                Argon2Parameters
                    .Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt)
                    .withIterations(appConfig.passwordHashConfig.iterations)
                    .withMemoryAsKB(appConfig.passwordHashConfig.memoryKb)
                    .withParallelism(appConfig.passwordHashConfig.parallelism)
                    .build()

            val generator = Argon2BytesGenerator()
            generator.init(params)

            val hash = ByteArray(appConfig.passwordHashConfig.hashLength)
            generator.generateBytes(password.toByteArray(), hash, 0, appConfig.passwordHashConfig.hashLength)

            return Result.Success(
                data = "${Base64.getEncoder().encodeToString(salt)}$${appConfig.passwordHashConfig.iterations}$${
                    Base64.getEncoder().encodeToString(hash)
                }",
                message = "Password hashed successfully",
            )
        } catch (e: IllegalArgumentException) {
            logger.error("Invalid argument in Argon2 hashing process", e)
            return Result.Error(
                HashError.InvalidArgument,
                data = null,
                message = "Invalid argument in Argon2 hashing process",
            )
        } catch (e: OutOfMemoryError) {
            logger.error("Out of memory during Argon2 hashing process", e)
            return Result.Error(
                HashError.OutOfMemoryError,
                data = null,
                message = "Out of memory during Argon2 hashing process",
            )
        } catch (e: Exception) {
            logger.error("Error hashing password", e)
            return Result.Error(HashError.HashGenerationFailed, data = null, message = "Error hashing password")
        }
    }

    override fun verifyPassword(
        password: String,
        storedHash: String,
    ): Result<Boolean, RootError> {
        try {
            val parts = storedHash.split("$")
            if (parts.size != 3) {
                return Result.Error(
                    HashError.InvalidHash,
                    data = null,
                    message = "Invalid hash format",
                )
            }

            val salt = Base64.getDecoder().decode(parts[0])
            val iterations =
                parts[1].toIntOrNull() ?: return Result.Error(
                    HashError.InvalidHash,
                    data = null,
                    message = "Invalid hash format",
                )
            val expectedHash = Base64.getDecoder().decode(parts[2])

            val params =
                Argon2Parameters
                    .Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt)
                    .withIterations(iterations)
                    .withMemoryAsKB(appConfig.passwordHashConfig.memoryKb)
                    .withParallelism(appConfig.passwordHashConfig.parallelism)
                    .build()

            val generator = Argon2BytesGenerator()
            generator.init(params)

            val computedHash = ByteArray(appConfig.passwordHashConfig.hashLength)
            generator.generateBytes(password.toByteArray(), computedHash, 0, appConfig.passwordHashConfig.hashLength)

            return Result.Success(
                data = computedHash.contentEquals(expectedHash),
                message = "Password verified successfully",
            )
        } catch (e: IllegalArgumentException) {
            logger.error("Invalid argument in Argon2 hashing process", e)
            return Result.Error(
                HashError.InvalidArgument,
                data = null,
                message = "Invalid argument in Argon2 hashing process",
            )
        } catch (e: OutOfMemoryError) {
            logger.error("Out of memory during Argon2 hashing process", e)
            return Result.Error(
                HashError.OutOfMemoryError,
                data = null,
                message = "Out of memory during Argon2 hashing process",
            )
        } catch (e: Exception) {
            logger.error("Error verifying password", e)
            return Result.Error(HashError.HashVerificationFailed, data = null, message = "Error verifying password")
        }
    }
}
