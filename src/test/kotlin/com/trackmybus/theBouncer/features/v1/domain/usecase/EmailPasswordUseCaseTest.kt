package com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword

import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword.EmailPasswordUseCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertTrue

class EmailPasswordUseCaseTest : KoinTest {
    private lateinit var emailPasswordUseCase: EmailPasswordUseCase
    private lateinit var databaseFactory: DatabaseFactory

    @Before
    fun setup() {
        configureKoinUnitTest()

        emailPasswordUseCase = get()
        databaseFactory = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun registerUser_withValidData_registersSuccessfully() =
        runBlocking {
            val email = "test@example.com"
            val password = "Password123#"
            val firstName = "John"
            val lastName = "Doe"
            val result = emailPasswordUseCase.registerUser(email, password, firstName, lastName)
            assertTrue(result.isSuccess())
        }

    @Test
    fun registerUser_withExistingEmail_returnsError() =
        runBlocking {
            val email = "existing@example.com"
            val password = "password123"
            val firstName = "John"
            val lastName = "Doe"
            emailPasswordUseCase.registerUser(email, password, firstName, lastName)
            val result = emailPasswordUseCase.registerUser(email, password, firstName, lastName)
            assertTrue(result.isFailure())
        }

    @Test
    fun loginUser_withValidCredentials_returnsTokens() =
        runBlocking {
            val email = "test@example.com"
            val password = "Password123#"
            val registerResult = emailPasswordUseCase.registerUser(email, password, "John", "Doe")
            print(registerResult)
            val result = emailPasswordUseCase.loginUser(email, password)
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull()?.accessToken != null)
            assertTrue(result.getDataOrNull()?.refreshToken != null)
        }

    @Test
    fun loginUser_withInvalidCredentials_returnsError() =
        runBlocking {
            val email = "test@example.com"
            val password = "wrongpassword"
            val result = emailPasswordUseCase.loginUser(email, password)
            assertTrue(result.isFailure())
        }
}
