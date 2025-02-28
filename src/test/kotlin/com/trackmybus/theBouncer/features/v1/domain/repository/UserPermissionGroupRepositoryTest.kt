package com.trackmybus.theBouncer.features.v1.domain.repository

import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup.PermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup
import com.trackmybus.theBouncer.features.v1.data.model.User
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.domain.repository.userPermissionGroup.UserPermissionGroupRepository
import io.ktor.util.logging.Logger
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserPermissionGroupRepositoryTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var userPermissionGroupRepository: UserPermissionGroupRepository
    private lateinit var userDao: UserDao
    private lateinit var permissionGroupDao: PermissionGroupDao
    private lateinit var logger: Logger

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        userPermissionGroupRepository = get()
        userDao = get()
        permissionGroupDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun getAll_returnsAllUserPermissionGroups() =
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
            var permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            permissionGroup = permissionGroupDao.addPermissionGroup(permissionGroup).getDataOrNull()!!
            val userPermissionGroup = UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id)
            userPermissionGroupRepository.add(userPermissionGroup)
            val result = userPermissionGroupRepository.getAll()
            assertTrue(result.isSuccess())
            assertEquals(1, result.getDataOrNull()?.size)
        }

    @Test
    fun getByUserId_returnsUserPermissionGroups() =
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
            var permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            permissionGroup = permissionGroupDao.addPermissionGroup(permissionGroup).getDataOrNull()!!
            val userPermissionGroup = UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id)
            userPermissionGroupRepository.add(userPermissionGroup)
            val result = userPermissionGroupRepository.getByUserId(user.id!!)
            assertTrue(result.isSuccess())
            assertEquals(1, result.getDataOrNull()?.size)
        }

    @Test
    fun getByUserId_returnsEmptyListForNonExistentUser() =
        runBlocking {
            val result = userPermissionGroupRepository.getByUserId(UUID.randomUUID())
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull()?.isEmpty() ?: false)
        }

    @Test
    fun add_addsUserPermissionGroupSuccessfully() =
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
            var permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            permissionGroup = permissionGroupDao.addPermissionGroup(permissionGroup).getDataOrNull()!!
            val userPermissionGroup = UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id)
            val result = userPermissionGroupRepository.add(userPermissionGroup)
            assertTrue(result.isSuccess())
            val retrievedUserPermissionGroup = userPermissionGroupRepository.getByUserId(user.id!!)
            assertEquals(1, retrievedUserPermissionGroup.getDataOrNull()?.size)
        }

    @Test
    fun add_addsMultipleUserPermissionGroupsSuccessfully() =
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
            var permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            permissionGroup = permissionGroupDao.addPermissionGroup(permissionGroup).getDataOrNull()!!
            val permissionGroup2 = permissionGroupDao.addPermissionGroup(permissionGroup).getDataOrNull()!!
            val userPermissionGroups =
                listOf(
                    UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id),
                    UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup2.id),
                )
            val result = userPermissionGroupRepository.add(userPermissionGroups)
            assertTrue(result.isSuccess())
            val retrievedUserPermissionGroups = userPermissionGroupRepository.getAll()
            assertEquals(2, retrievedUserPermissionGroups.getDataOrNull()?.size)
        }

    @Test
    fun update_updatesUserPermissionGroupSuccessfully() =
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
            var permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            permissionGroup = permissionGroupDao.addPermissionGroup(permissionGroup).getDataOrNull()!!
            var userPermissionGroup = UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id)
            userPermissionGroup = userPermissionGroupRepository.add(userPermissionGroup).getDataOrNull()!!
            userPermissionGroup = userPermissionGroup.copy(permissionGroupId = 1)
            val result = userPermissionGroupRepository.update(userPermissionGroup)
            assertTrue(result.isSuccess())
            val retrievedUserPermissionGroup = userPermissionGroupRepository.getByUserId(user.id!!)
            assertEquals(1, retrievedUserPermissionGroup.getDataOrNull()?.first()?.permissionGroupId)
        }

    @Test
    fun deleteById_deletesUserPermissionGroupSuccessfully() =
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
            var permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            permissionGroup = permissionGroupDao.addPermissionGroup(permissionGroup).getDataOrNull()!!
            var userPermissionGroup = UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id)
            userPermissionGroup = userPermissionGroupRepository.add(userPermissionGroup).getDataOrNull()!!
            val result = userPermissionGroupRepository.deleteById(userPermissionGroup)
            assertTrue(result.isSuccess())
            val retrievedUserPermissionGroup = userPermissionGroupRepository.getByUserId(user.id!!)
            assertTrue(retrievedUserPermissionGroup.getDataOrNull()?.isEmpty() ?: false)
        }

    @Test
    fun deleteAll_deletesAllUserPermissionGroupsSuccessfully() =
        runBlocking {
            val user =
                User(
                    firstName = "John",
                    lastName = "Doe",
                    hashedPassword = "hashedPassword",
                    provider = AuthProvider.EMAIL_PASSWORD,
                    createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                )
            val permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            val userPermissionGroups =
                listOf(
                    UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id),
                    UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id),
                )
            userPermissionGroupRepository.add(userPermissionGroups)
            val result = userPermissionGroupRepository.deleteAll()
            assertTrue(result.isSuccess())
            val retrievedUserPermissionGroups = userPermissionGroupRepository.getAll()
            assertTrue(retrievedUserPermissionGroups.getDataOrNull()?.isEmpty() ?: false)
        }
}
