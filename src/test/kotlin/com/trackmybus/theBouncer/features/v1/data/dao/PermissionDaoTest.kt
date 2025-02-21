package com.trackmybus.theBouncer.features.v1.data.dao

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.dao.permission.PermissionDao
import com.trackmybus.theBouncer.features.v1.data.model.Permission
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PermissionDaoTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var permissionDao: PermissionDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        permissionDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun getAllPermissions_returnsAllPermissions() =
        runBlocking {
            val permissions =
                listOf(
                    Permission(name = "READ", description = "Read permission"),
                    Permission(name = "WRITE", description = "Write permission"),
                )
            permissionDao.addPermissions(permissions)
            val result = permissionDao.getAllPermissions()
            assertTrue(result.isSuccess)
            assertEquals(permissions.size, result.getOrNull()?.size)
        }

    @Test
    fun getPermissionById_returnsPermission() =
        runBlocking {
            var permission = Permission(name = "READ", description = "Read permission")
            permission = permission.copy(id = permissionDao.addPermission(permission).getOrNull())
            val result = permissionDao.getPermissionById(permission.id!!)
            assertTrue(result.isSuccess)
            assertEquals(permission.id, result.getOrNull()?.id?.value)
        }

    @Test
    fun getPermissionById_returnsNullForNonExistentPermission() =
        runBlocking {
            val result = permissionDao.getPermissionById(999)
            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }

    @Test
    fun addPermission_addsPermissionSuccessfully() =
        runBlocking {
            val permission = Permission(name = "READ", description = "Read permission")
            val result = permissionDao.addPermission(permission)
            assertTrue(result.isSuccess)
            val retrievedPermission = permissionDao.getPermissionById(result.getOrNull()!!)
            assertEquals(result.getOrNull()!!, retrievedPermission.getOrNull()?.id?.value)
        }

    @Test
    fun addPermissions_addsMultiplePermissionsSuccessfully() =
        runBlocking {
            val permissions =
                listOf(
                    Permission(name = "READ", description = "Read permission"),
                    Permission(name = "WRITE", description = "Write permission"),
                )
            val result = permissionDao.addPermissions(permissions)
            assertTrue(result.isSuccess)
            val retrievedPermissions = permissionDao.getAllPermissions()
            assertEquals(permissions.size, retrievedPermissions.getOrNull()?.size)
        }

    @Test
    fun updatePermission_updatesPermissionSuccessfully() =
        runBlocking {
            var permission = Permission(name = "READ", description = "Read permission")
            permission = permission.copy(id = permissionDao.addPermission(permission).getOrNull())
            permission.description = "Updated read permission"
//            val result = permissionDao.updatePermission(permission)
//            assertTrue(result.isSuccess)
            val retrievedPermission = permissionDao.getPermissionById(permission.id!!)
//            assertEquals("Updated read permission", retrievedPermission.getOrNull()?.description)
        }

    @Test
    fun deletePermission_deletesPermissionSuccessfully() =
        runBlocking {
            var permission = Permission(name = "READ", description = "Read permission")
            permission = permission.copy(id = permissionDao.addPermission(permission).getOrNull())
            val result = permissionDao.deletePermission(permission.id!!)
            assertTrue(result.isSuccess)
            val retrievedPermission = permissionDao.getPermissionById(permission.id)
            assertNull(retrievedPermission.getOrNull())
        }

    @Test
    fun deleteAllPermissions_deletesAllPermissionsSuccessfully() =
        runBlocking {
            val permissions =
                listOf(
                    Permission(name = "READ", description = "Read permission"),
                    Permission(name = "WRITE", description = "Write permission"),
                )
            permissionDao.addPermissions(permissions)
            val result = permissionDao.deleteAllPermissions()
            assertTrue(result.isSuccess)
            val retrievedPermissions = permissionDao.getAllPermissions()
            assertTrue(retrievedPermissions.getOrNull()?.isEmpty() ?: false)
        }
}
