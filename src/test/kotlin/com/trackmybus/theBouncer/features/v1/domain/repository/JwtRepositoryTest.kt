package com.trackmybus.theBouncer.features.v1.domain.repository

import com.trackmybus.theBouncer.config.setupConfig
import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.local.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.local.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.local.model.User
import com.trackmybus.theBouncer.features.v1.domain.repository.jwt.JwtRepository
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID
import kotlin.test.Test

class JwtRepositoryTest : KoinTest {
    private lateinit var jwtRepository: JwtRepository
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var userDao: UserDao

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
        databaseFactory = get()
        jwtRepository = get()
        userDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun generateAccessToken_withValidUser_returnsToken() =
        runBlocking {
            val user =
                User(
                    id = UUID.randomUUID(),
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val result = jwtRepository.generateAccessToken(user)
            assertTrue(result.isSuccess())
        }

    @Test
    fun generateAccessToken_withInvalidUser_returnsError() =
        runBlocking {
            val user =
                User(
                    firstName = "",
                    lastName = "",
                    hashedPassword = "",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val result = jwtRepository.generateAccessToken(user)
            assertTrue(result.isFailure())
        }

    @Test
    fun generateRefreshToken_withValidUser_returnsToken() =
        runBlocking {
            val user =
                User(
                    id = UUID.randomUUID(),
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val result = jwtRepository.generateRefreshToken(user)
            assertTrue(result.isFailure())
        }

    @Test
    fun generateRefreshToken_withInvalidUser_returnsError() =
        runBlocking {
            val user =
                User(
                    firstName = "",
                    lastName = "",
                    hashedPassword = "",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val result = jwtRepository.generateRefreshToken(user)
            assertTrue(result.isFailure())
        }

    @Test
    fun validateAccessToken_withValidToken_returnsTrue() =
        runBlocking {
            var user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            user = userDao.addUser(user).getDataOrNull()!!
            val resultAccess = jwtRepository.generateAccessToken(user)
            val validToken = resultAccess.getDataOrNull() ?: ""
            val result = jwtRepository.validateAccessToken(validToken)
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull() ?: false)
        }

    @Test
    fun validateAccessToken_withInvalidToken_returnsFalse() =
        runBlocking {
            val invalidToken = "invalidToken"
            val result = jwtRepository.validateAccessToken(invalidToken)
            assertTrue(result.isFailure())
        }

    @Test
    fun validateRefreshToken_withValidToken_returnsTrue() =
        runBlocking {
            var user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            user = userDao.addUser(user).getDataOrNull()!!
            val resultRefresh = jwtRepository.generateRefreshToken(user)
            val validToken = resultRefresh.getDataOrNull() ?: ""
            val result = jwtRepository.validateRefreshToken(validToken)
            assertTrue(result.isSuccess())
        }

    @Test
    fun validateRefreshToken_withInvalidToken_returnsFalse() =
        runBlocking {
            val invalidUser = User()
            val result = jwtRepository.validateRefreshToken(invalidUser.toString())
            assertTrue(result.isFailure())
        }

    @Test
    fun refreshAccessToken_withInvalidRefreshToken_returnsError() =
        runBlocking {
            val invalidRefreshToken = "invalidRefreshToken"
            val result = jwtRepository.refreshAccessToken(invalidRefreshToken)
            assertTrue(result.isFailure())
        }

    @Test
    fun revokeSession_withInvalidRefreshToken_returnsError() =
        runBlocking {
            val invalidRefreshToken = "invalidRefreshToken"
            val result = jwtRepository.revokeSession(invalidRefreshToken)
            assertTrue(result.isFailure())
        }
}
