package com.trackmybus.theBouncer.features.v1.domain.repository.passowrdHash

import com.trackmybus.theBouncer.config.AppConfig
import io.ktor.util.logging.Logger
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.security.SecureRandom
import java.util.Base64

class PasswordHashRepositoryImpl(
    private val logger: Logger,
    private val appConfig: AppConfig,
) : PasswordHashRepository {
    override fun hashPassword(password: String): String {
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

            return "${Base64.getEncoder().encodeToString(salt)}$${appConfig.passwordHashConfig.iterations}$${
                Base64.getEncoder().encodeToString(hash)
            }"
        } catch (e: Exception) {
            logger.error("Error hashing password", e)
            throw e
        }
    }

    override fun verifyPassword(
        password: String,
        storedHash: String,
    ): Boolean {
        try {
            val parts = storedHash.split("$")
            if (parts.size != 3) return false

            val salt = Base64.getDecoder().decode(parts[0])
            val iterations = parts[1].toIntOrNull() ?: return false
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

            return computedHash.contentEquals(expectedHash)
        } catch (e: Exception) {
            logger.error("Error verifying password", e)
            throw e
        }
    }
}
