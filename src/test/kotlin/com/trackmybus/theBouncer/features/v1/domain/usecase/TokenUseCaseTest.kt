package com.trackmybus.theBouncer.features.v1.domain.usecase

import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword.EmailPasswordUseCase
import com.trackmybus.theBouncer.features.v1.domain.usecase.token.TokenUseCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertTrue

class TokenUseCaseTest : KoinTest {
    private lateinit var tokenUseCase: TokenUseCase
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var emailPasswordUseCase: EmailPasswordUseCase
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        configureKoinUnitTest()
        tokenUseCase = get()
        databaseFactory = get()
        emailPasswordUseCase = get()
        userDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun refreshToken_returnsTokensDto_onSuccess() =
        runBlocking {
            val email = "test@example.com"
            val password = "Password123#"
            val registerResult = emailPasswordUseCase.registerUser(email, password, "John", "Doe")
            print(registerResult)
            val loginResult = emailPasswordUseCase.loginUser(email, password)

            val result = tokenUseCase.refreshToken(loginResult.getDataOrNull()!!.refreshToken)

            assertTrue(result.isSuccess())
        }

    @Test
    fun refreshToken_returnsError_onFailure() =
        runBlocking {
            val refreshToken = "invalidRefreshToken"

            val result = tokenUseCase.refreshToken(refreshToken)

            assertTrue(result.isFailure())
        }

    @Test
    fun validateAccessToken_returnsTrue_onValidToken() =
        runBlocking {
            val email = "test@example.com"
            val password = "Password123#"
            val registerResult = emailPasswordUseCase.registerUser(email, password, "John", "Doe")
            print(registerResult)
            val loginResult = emailPasswordUseCase.loginUser(email, password)

            val result = tokenUseCase.validateAccessToken(loginResult.getDataOrNull()!!.accessToken)

            assertTrue(result.isSuccess())
        }

    @Test
    fun validateAccessToken_returnsFalse_onInvalidToken() =
        runBlocking {
            val accessToken = "invalidAccessToken"

            val result = tokenUseCase.validateAccessToken(accessToken)

            assertTrue(result.isFailure())
        }

    @Test
    fun validateRefreshToken_returnsTrue_onValidToken() =
        runBlocking {
            val email = "test@example.com"
            val password = "Password123#"
            val registerResult = emailPasswordUseCase.registerUser(email, password, "John", "Doe")
            print(registerResult)
            val loginResult = emailPasswordUseCase.loginUser(email, password)

            val result = tokenUseCase.validateRefreshToken(loginResult.getDataOrNull()!!.refreshToken)

            assertTrue(result.isSuccess())
        }

    @Test
    fun validateRefreshToken_returnsFalse_onInvalidToken() =
        runBlocking {
            val refreshToken = "invalidRefreshToken"

            val result = tokenUseCase.validateRefreshToken(refreshToken)

            assertTrue(result.isFailure())
        }

    @Test
    fun revokeSession_returnsUnit_onSuccess() =
        runBlocking {
            val email = "test@example.com"
            val password = "Password123#"
            val registerResult = emailPasswordUseCase.registerUser(email, password, "John", "Doe")
            print(registerResult)
            val loginResult = emailPasswordUseCase.loginUser(email, password)
            val result = tokenUseCase.revokeSession(loginResult.getDataOrNull()!!.refreshToken)

            assertTrue(result.isSuccess())
        }

    @Test
    fun revokeSession_returnsError_onFailure() =
        runBlocking {
            val refreshToken = "invalidRefreshToken"

            val result = tokenUseCase.revokeSession(refreshToken)

            assertTrue(result.isFailure())
        }
}
