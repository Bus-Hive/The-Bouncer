package com.trackmybus.theBouncer.features.v1.data.local.dao

import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.local.dao.permissionGroup.PermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.local.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.local.dao.userPermissionGroup.UserPermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.local.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.local.model.PermissionGroup
import com.trackmybus.theBouncer.features.v1.data.local.model.User
import com.trackmybus.theBouncer.features.v1.data.local.model.UserPermissionGroup
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.junit.After
import org.junit.Before
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertTrue

class UserPermissionGroupDaoTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var permissionGroupDao: PermissionGroupDao
    private lateinit var userDao: UserDao
    private lateinit var userPermissionGroupDao: UserPermissionGroupDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        userDao = get()
        userPermissionGroupDao = get()
        permissionGroupDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun addUserPermissionGroup_withValidData_addsSuccessfully() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id)
            val result = userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isSuccess())
        }

    @Test
    fun updateUserPermissionGroup_withValidData_updatesSuccessfully() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id)
            userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)
            val updatedUserPermissionGroup = userPermissionGroup.copy(permissionGroupId = 1)
            val result = userPermissionGroupDao.updateUserPermissionGroup(updatedUserPermissionGroup)
            assertTrue(result.isSuccess())
        }

    @Test
    fun getAllUserPermissionGroups_returnsAllUserPermissions() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id)
            userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)
            val result = userPermissionGroupDao.getAllUserPermissionGroups()
            assertTrue(result.isSuccess())
            assertEquals(1, result.getDataOrNull()?.size)
        }

    @Test
    fun addUserPermissionGroup_withNullUserId_returnsError() =
        runBlocking {
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = null, permissionGroupId = permissionGroup?.id)
            val result = userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isFailure())
        }

    @Test
    fun addUserPermissionGroup_withNullPermissionGroupId_returnsError() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "Jane",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = null)
            val result = userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isFailure())
        }

    @Test
    fun updateUserPermissionGroup_withNullUserId_returnsError() =
        runBlocking {
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = null, permissionGroupId = permissionGroup?.id)
            val result = userPermissionGroupDao.updateUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isFailure())
        }

    @Test
    fun updateUserPermissionGroup_withNullPermissionGroupId_returnsError() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "Jane",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = null)
            val result = userPermissionGroupDao.updateUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isFailure())
        }

    @Test
    fun deleteUserPermissionGroup_withNullUserId_returnsError() =
        runBlocking {
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = null, permissionGroupId = permissionGroup?.id)
            val result = userPermissionGroupDao.deleteUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isFailure())
        }

    @Test
    fun deleteUserPermissionGroup_withNullPermissionGroupId_returnsError() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "Jane",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = null)
            val result = userPermissionGroupDao.deleteUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isFailure())
        }

    @Test
    fun addUserPermissionGroups_withValidData_addsSuccessfully() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroups =
                listOf(UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id))
            val result = userPermissionGroupDao.addUserPermissionGroups(userPermissionGroups)
            assertTrue(result.isSuccess())
        }

    @Test
    fun addUserPermissionGroups_withMixedValidAndInvalidData_returnsError() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val invalidUserPermissionGroup =
                UserPermissionGroup(userId = UUID.randomUUID(), permissionGroupId = 1)
            val userPermissionGroups =
                listOf(
                    UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id),
                    invalidUserPermissionGroup,
                )
            val result = userPermissionGroupDao.addUserPermissionGroups(userPermissionGroups)
            assertTrue(result.isFailure())
        }

    @Test
    fun addUserPermissionGroups_withDuplicateEntries_returnsError() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id)
            val userPermissionGroups = listOf(userPermissionGroup, userPermissionGroup)
            val result = userPermissionGroupDao.addUserPermissionGroups(userPermissionGroups)
            assertTrue(result.isFailure())
        }

    @Test
    fun deleteAllUserPermissionGroups_deletesSuccessfully() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id)
            userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)
            val result = userPermissionGroupDao.deleteAllUserPermissionGroups()
            assertTrue(result.isSuccess())
            val allUserPermissionGroups = userPermissionGroupDao.getAllUserPermissionGroups()
            assertTrue(allUserPermissionGroups.getDataOrNull()?.isEmpty() ?: false)
        }

    @Test
    fun deleteAllUserPermissionGroups_withNoExistingGroups_returnsSuccess() =
        runBlocking {
            val result = userPermissionGroupDao.deleteAllUserPermissionGroups()
            assertTrue(result.isSuccess())
        }

    @Test
    fun getUserPermissionGroupsByUserId_withValidUserId_returnsUserPermissionGroups() =
        runBlocking {
            val user =
                userDao
                    .addUser(
                        User(
                            firstName = "John",
                            lastName = "Doe",
                            hashedPassword = "hashedPassword",
                            provider = AuthProvider.EMAIL_PASSWORD,
                            createdAt = LocalDateTime(2021, 1, 1, 0, 0),
                        ),
                    ).getDataOrNull()
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val userPermissionGroup = UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id)
            userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)
            val result = userPermissionGroupDao.getUserPermissionGroupsByUserId(user?.id!!)
            assertTrue(result.isSuccess())
            assertEquals(1, result.getDataOrNull()?.size)
        }

    @Test
    fun getUserPermissionGroupsByUserId_withInvalidUserId_returnsEmptyList() =
        runBlocking {
            val result = userPermissionGroupDao.getUserPermissionGroupsByUserId(UUID.randomUUID())
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull()?.isEmpty() ?: false)
        }
}
