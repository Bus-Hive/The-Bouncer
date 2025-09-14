package app.bushive.theBouncer.features.v1.data.local.dao

import app.bushive.theBouncer.core.result.ResultHandler.isFailure
import app.bushive.theBouncer.core.result.ResultHandler.isSuccess
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.di.configureKoinUnitTest
import app.bushive.theBouncer.features.v1.data.local.dao.user.UserDao
import app.bushive.theBouncer.features.v1.data.local.model.AuthProvider
import app.bushive.theBouncer.features.v1.data.local.model.User
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID

class UserDaoTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        userDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun getAllUsers_returnsAllUsers() =
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
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            userDao.addUsers(users)
            val result = userDao.getAllUsers()
            assertTrue(result.isSuccess())
            assertEquals(users.size, result.getDataOrNull()?.size)
        }

    @Test
    fun getUserById_returnsUser() =
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
            val result = userDao.getUserById(user.id!!)
            assertTrue(result.isSuccess())
            assertEquals(user, result.getDataOrNull())
        }

    @Test
    fun getUserById_returnsNullForNonExistentUser() =
        runBlocking {
            val result = userDao.getUserById(UUID.randomUUID())
            assertTrue(result.isFailure())
            assertNull(result.getDataOrNull())
        }

    @Test
    fun addUser_addsUserSuccessfully() =
        runBlocking {
            var user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val result = userDao.addUser(user)
            user = result.getDataOrNull()!!
            assertTrue(result.isSuccess())
            val retrievedUser = userDao.getUserById(user.id!!)
            assertEquals(user.id, retrievedUser.getDataOrNull()?.id)
        }

    @Test
    fun addUsers_addsMultipleUsersSuccessfully() =
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
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            val result = userDao.addUsers(users)
            assertTrue(result.isSuccess())
            val retrievedUsers = userDao.getAllUsers()
            assertEquals(users.size, retrievedUsers.getDataOrNull()?.size)
        }

    @Test
    fun updateUser_updatesUserSuccessfully() =
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
            val result = userDao.updateUser(user)
            assertTrue(result.isSuccess())
            val retrievedUser = userDao.getUserById(user.id!!)
            assertEquals(user.lastName, retrievedUser.getDataOrNull()?.lastName)
        }

    @Test
    fun deleteUser_deletesUserSuccessfully() =
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
            val result = userDao.deleteUser(user.id!!)
            assertTrue(result.isSuccess())
            val retrievedUser = userDao.getUserById(user.id!!)
            assertNull(retrievedUser.getDataOrNull())
        }

    @Test
    fun deleteAllUsers_deletesAllUsersSuccessfully() =
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
                        firstName = "John",
                        lastName = "Doe",
                        hashedPassword = "hashedPassword",
                        provider = AuthProvider.EMAIL_PASSWORD,
                        createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                    ),
                )
            userDao.addUsers(users)
            val result = userDao.deleteAllUsers()
            assertTrue(result.isSuccess())
            val retrievedUsers = userDao.getAllUsers()
            assertTrue(retrievedUsers.getDataOrNull()?.isEmpty() ?: false)
        }
}
