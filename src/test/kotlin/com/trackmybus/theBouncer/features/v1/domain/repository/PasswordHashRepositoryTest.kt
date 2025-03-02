package com.trackmybus.theBouncer.features.v1.domain.repository

import com.trackmybus.theBouncer.config.setupConfig
import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.domain.repository.passowrdHash.PasswordHashRepository
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertTrue

class PasswordHashRepositoryTest : KoinTest {
    private lateinit var passwordHashRepository: PasswordHashRepository

    @Before
    fun setup() {
        configureKoinUnitTest()
        testApplication {
            application {
                (environment.config as MapApplicationConfig).apply {
                    put("ktor.server.isProd", "false")
                    put("postgres.driverClass", "org.h2.Driver")
                    put("postgres.jdbcURL", "jdbc:h2:mem:;DATABASE_TO_UPPER=false;MODE=MYSQL")
                    put("postgres.database", "")
                    put("postgres.host", "localhost")
                    put("postgres.port", "5432")
                    put("postgres.user", "")
                    put("postgres.password", "")
                    put("postgres.maxPoolSize", "1")
                    put("postgres.autoCommit", "true")

                    put("redis.host", "localhost")
                    put("redis.port", "6379")
                    put("redis.password", "password")
                    put("redis.database", "0")

                    put("password.saltLength", "16")
                    put("password.iterations", "1")
                    put("password.hashLength", "64")
                    put("password.memoryKb", "1024")
                    put("password.parallelism", "1")

                    put("jwt.secret", "secret")
                    put("jwt.issuer", "issuer")
                    put("jwt.audience", "audience")
                    put("jwt.realm", "3600")
                    put("jwt.accessTokenValiditySeconds", "3600")
                    put("jwt.refreshTokenValiditySeconds", "3600")

                    put("google.clientId", "clientId")
                    put("google.clientSecret", "clientSecret")
                    put("google.redirectUri", "redirectUri")
                    put("google.grantType", "grantType")
                }
                setupConfig()
            }
        }
        passwordHashRepository = get()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun hashPassword_withValidPassword_returnsHashedPassword() =
        runBlocking {
            val password = "validPassword123"
            val result = passwordHashRepository.hashPassword(password)
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull() != null)
        }

    @Test
    fun hashPassword_withEmptyPassword_returnsError() =
        runBlocking {
            val password = ""
            val result = passwordHashRepository.hashPassword(password)
            assertTrue(result.isFailure())
        }

    @Test
    fun verifyPassword_withCorrectPassword_returnsTrue() =
        runBlocking {
            val password = "validPassword123"
            val hashedPassword = passwordHashRepository.hashPassword(password).getDataOrNull()!!
            val result = passwordHashRepository.verifyPassword(password, hashedPassword)
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull() ?: false)
        }

    @Test
    fun verifyPassword_withIncorrectPassword_returnsFalse() =
        runBlocking {
            val password = "validPassword123"
            val hashedPassword = passwordHashRepository.hashPassword(password).getDataOrNull()!!
            val result = passwordHashRepository.verifyPassword("wrongPassword", hashedPassword)
            assertTrue(result.isSuccess())
            assertFalse(result.getDataOrNull() ?: true)
        }

    @Test
    fun verifyPassword_withEmptyPassword_returnsError() =
        runBlocking {
            val password = "validPassword123"
            val hashedPassword = passwordHashRepository.hashPassword(password).getDataOrNull()!!
            val result = passwordHashRepository.verifyPassword("", hashedPassword)
            print(result)
            assertTrue(result.isSuccess())
            assertFalse(result.getDataOrNull() ?: true)
        }
}
