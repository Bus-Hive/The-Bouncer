package com.trackmybus.theBouncer.features.v1.domain.repository

import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.model.User
import com.trackmybus.theBouncer.features.v1.domain.repository.user.UserRepository
import io.ktor.util.logging.Logger
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRepositoryTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var userRepository: UserRepository
    private lateinit var logger: Logger

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        userRepository = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun getAll_returnsAllUsers() =
        runBlocking {
            val users =
                listOf(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    User(
                        firstName = "Jane",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            userRepository.add(users)
            val result = userRepository.getAll()
            assertTrue(result.isSuccess())
            assertEquals(users.size, result.getDataOrNull()?.size)
        }

    @Test
    fun getById_returnsUser() =
        runBlocking {
            val user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val addedUser = userRepository.add(user).getDataOrNull()!!
            val result = userRepository.getById(addedUser.id!!)
            assertTrue(result.isSuccess())
            assertEquals(addedUser.id, result.getDataOrNull()?.id)
        }

    @Test
    fun getById_returnsNullForNonExistentUser() =
        runBlocking {
            val result = userRepository.getById(UUID.randomUUID())
            assertTrue(result.isFailure())
        }

    @Test
    fun getByEmail_returnsUser() =
        runBlocking {
            val user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    email = "john.doe@example.com",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            userRepository.add(user)
            val result = userRepository.getByEmail("john.doe@example.com")
            assertTrue(result.isSuccess())
            assertEquals(user.email, result.getDataOrNull()?.email)
        }

    @Test
    fun getByEmail_returnsNullForNonExistentEmail() =
        runBlocking {
            val result = userRepository.getByEmail("nonexistent@example.com")
            assertTrue(result.isFailure())
        }

    @Test
    fun isEmailUnique_returnsTrueForUniqueEmail() =
        runBlocking {
            val result = userRepository.isEmailUnique("unique@example.com")
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull() == true)
        }

    @Test
    fun isEmailUnique_returnsFalseForExistingEmail() =
        runBlocking {
            val user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    email = "john.doe@example.com",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            userRepository.add(user)
            val result = userRepository.isEmailUnique("john.doe@example.com")
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull() == false)
        }

    @Test
    fun add_addsUserSuccessfully() =
        runBlocking {
            val user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val result = userRepository.add(user)
            assertTrue(result.isSuccess())
            val retrievedUser = userRepository.getAll()
            assertEquals(1, retrievedUser.getDataOrNull()?.size)
        }

    @Test
    fun add_addsMultipleUsersSuccessfully() =
        runBlocking {
            val users =
                listOf(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    User(
                        firstName = "Jane",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            val result = userRepository.add(users)
            assertTrue(result.isSuccess())
            val retrievedUsers = userRepository.getAll()
            assertEquals(users.size, retrievedUsers.getDataOrNull()?.size)
        }

    @Test
    fun update_updatesUserSuccessfully() =
        runBlocking {
            var user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            user = userRepository.add(user).getDataOrNull()!!
            user.lastName = "Smith"
            val result = userRepository.update(user)
            assertTrue(result.isSuccess())
            val retrievedUser = userRepository.getById(user.id!!)
            assertEquals("Smith", retrievedUser.getDataOrNull()?.lastName)
        }

    @Test
    fun delete_deletesUserSuccessfully() =
        runBlocking {
            var user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            user = userRepository.add(user).getDataOrNull()!!
            val result = userRepository.delete(user.id!!)
            assertTrue(result.isSuccess())
            val retrievedUser = userRepository.getById(user.id!!)
            assertTrue(retrievedUser.isFailure())
        }

    @Test
    fun deleteAll_deletesAllUsersSuccessfully() =
        runBlocking {
            val users =
                listOf(
                    User(
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                    User(
                        firstName = "Jane",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            userRepository.add(users)
            val result = userRepository.deleteAll()
            assertTrue(result.isSuccess())
            val retrievedUsers = userRepository.getAll()
            assertTrue(retrievedUsers.getDataOrNull()?.isEmpty() ?: false)
        }
}
