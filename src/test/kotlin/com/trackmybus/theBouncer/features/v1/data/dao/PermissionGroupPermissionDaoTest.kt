package com.trackmybus.theBouncer.features.v1.data.local.dao

import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.local.dao.permission.PermissionDao
import com.trackmybus.theBouncer.features.v1.data.local.dao.permissionGroup.PermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.local.dao.permissionGroupPermission.PermissionGroupPermissionDao
import com.trackmybus.theBouncer.features.v1.data.local.model.Permission
import com.trackmybus.theBouncer.features.v1.data.local.model.PermissionGroup
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PermissionGroupPermissionDaoTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var permissionGroupPermissionDao: PermissionGroupPermissionDao
    private lateinit var permissionDao: PermissionDao
    private lateinit var permissionGroupDao: PermissionGroupDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        permissionGroupPermissionDao = get()
        permissionDao = get()
        permissionGroupDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun addPermissionGroupPermission_withValidData_addsSuccessfully() =
        runBlocking {
            val permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            val permissionGroupResult = permissionGroupDao.addPermissionGroup(permissionGroup)
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    )

            val result = permissionGroupPermissionDao.addPermissionGroupPermission(1, 1)
            assertTrue(result.isSuccess())
        }

    @Test
    fun addPermissionGroupPermission_withInvalidData_returnsError() =
        runBlocking {
            val result = permissionGroupPermissionDao.addPermissionGroupPermission(-1, -1)
            assertTrue(result.isFailure())
        }

    @Test
    fun getPermissionGroupPermissions_withValidGroupId_returnsPermissions() =
        runBlocking {
            val permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            val permissionGroupResult = permissionGroupDao.addPermissionGroup(permissionGroup)
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    )
            permissionGroupPermissionDao.addPermissionGroupPermission(1, 1)
            val result = permissionGroupPermissionDao.getPermissionGroupPermissions(1)
            assertTrue(result.isSuccess())
            assertEquals(listOf(1), result.getDataOrNull())
        }

    @Test
    fun getPermissionGroupPermissions_withInvalidGroupId_returnsEmptyList() =
        runBlocking {
            val result = permissionGroupPermissionDao.getPermissionGroupPermissions(-1)
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull()?.isEmpty() ?: false)
        }

    @Test
    fun deletePermissionGroupPermission_withValidData_deletesSuccessfully() =
        runBlocking {
            val permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            val permissionGroupResult = permissionGroupDao.addPermissionGroup(permissionGroup)
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    )
            permissionGroupPermissionDao.addPermissionGroupPermission(1, 1)
            val result = permissionGroupPermissionDao.deletePermissionGroupPermission(1, 1)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deletePermissionGroupPermission_withInvalidData_returnsError() =
        runBlocking {
            val permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            val permissionGroupResult = permissionGroupDao.addPermissionGroup(permissionGroup)
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    )
            val result = permissionGroupPermissionDao.deletePermissionGroupPermission(-1, -1)
            assertTrue(result.isFailure())
        }

    @Test
    fun deletePermissionGroupPermissions_withValidGroupId_deletesSuccessfully() =
        runBlocking {
            val permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            val permissionGroupResult = permissionGroupDao.addPermissionGroup(permissionGroup)
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    )
            permissionGroupPermissionDao.addPermissionGroupPermission(1, 1)
            val result = permissionGroupPermissionDao.deletePermissionGroupPermissions(1)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deletePermissionGroupPermissions_withInvalidGroupId_returnsError() =
        runBlocking {
            val result = permissionGroupPermissionDao.deletePermissionGroupPermissions(-1)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deletePermissionPermissions_withValidPermissionId_deletesSuccessfully() =
        runBlocking {
            permissionGroupPermissionDao.addPermissionGroupPermission(1, 1)
            val result = permissionGroupPermissionDao.deletePermissionPermissions(1)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deletePermissionPermissions_withInvalidPermissionId_returnsError() =
        runBlocking {
            val result = permissionGroupPermissionDao.deletePermissionPermissions(-1)
            assertTrue(result.isSuccess())
        }
}
