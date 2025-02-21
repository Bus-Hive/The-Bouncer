package com.trackmybus.theBouncer.features.v1.data.dao

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.dao.permission.PermissionDao
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup.UserPermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.model.Permission
import com.trackmybus.theBouncer.features.v1.data.model.User
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Before
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID
import kotlin.test.Test

class UserPermissionDaoTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var permissionDao: PermissionDao
    private lateinit var userDao: UserDao
    private lateinit var userPermissionGroupDao: UserPermissionGroupDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        permissionDao = get()
        userDao = get()
        userPermissionGroupDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun getUserPermissionByUserId_returnsUserPermissions() =
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
                    ).getOrNull()

            val permission =
                permissionDao
                    .addPermission(
                        Permission(name = "READ", description = "Read permission"),
                    ).getOrNull()

            val userPermissionGroup = UserPermissionGroup(userId = user, permissionGroupId = permission)
            userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)

            val result = userPermissionGroupDao.getUserPermissionGroupsByUserId(user!!)
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
        }

    @Test
    fun getUserPermissionByUserId_returnsEmptyListForNonExistentUser() =
        runBlocking {
            val result = userPermissionGroupDao.getUserPermissionGroupsByUserId(UUID.randomUUID())
            assertTrue(result.isSuccess)
            assertTrue(result.getOrNull()?.isEmpty() ?: false)
        }

    @Test
    fun addUserPermission_addsUserPermissionSuccessfully() =
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
                    ).getOrNull()

            val permission =
                permissionDao
                    .addPermission(
                        Permission(name = "READ", description = "Read permission"),
                    ).getOrNull()

            val userPermissionGroup = UserPermissionGroup(userId = user, permissionGroupId = permission)
            val result = userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isSuccess)
        }

    @Test
    fun addUserPermissions_addsMultipleUserPermissionsSuccessfully() =
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
                    ).getOrNull()

            val permissions =
                listOf(
                    Permission(name = "READ", description = "Read permission"),
                    Permission(name = "WRITE", description = "Write permission"),
                ).map { permissionDao.addPermission(it).getOrNull() }

            val userPermissionGroups = permissions.map { UserPermissionGroup(userId = user, permissionGroupId = it) }
            val result = userPermissionGroupDao.addUserPermissionGroups(userPermissionGroups)
            assertTrue(result.isSuccess)
        }

    @Test
    fun deleteUserPermission_deletesUserPermissionSuccessfully() =
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
                    ).getOrNull()

            val permission =
                permissionDao
                    .addPermission(
                        Permission(name = "READ", description = "Read permission"),
                    ).getOrNull()

            val userPermissionGroup = UserPermissionGroup(userId = user, permissionGroupId = permission)
            userPermissionGroupDao.addUserPermissionGroup(userPermissionGroup)

            val result = userPermissionGroupDao.deleteUserPermissionGroup(userPermissionGroup)
            assertTrue(result.isSuccess)
        }

    @Test
    fun deleteAllUserPermissions_deletesAllUserPermissionsSuccessfully() =
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
                    ).getOrNull()

            val permissions =
                listOf(
                    Permission(name = "READ", description = "Read permission"),
                    Permission(name = "WRITE", description = "Write permission"),
                ).map { permissionDao.addPermission(it).getOrNull() }

            val userPermissionGroups = permissions.map { UserPermissionGroup(userId = user, permissionGroupId = it) }
            userPermissionGroupDao.addUserPermissionGroups(userPermissionGroups)

            val result = userPermissionGroupDao.deleteAllUserPermissionGroups()
            assertTrue(result.isSuccess)
            val retrievedUserPermissions = userPermissionGroupDao.getAllUserPermissionGroups()
            assertTrue(retrievedUserPermissions.getOrNull()?.isEmpty() ?: false)
        }
}
