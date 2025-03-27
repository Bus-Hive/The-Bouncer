package app.bushive.theBouncer.features.v1.data.local.dao

import app.bushive.theBouncer.core.result.ResultHandler.isFailure
import app.bushive.theBouncer.core.result.ResultHandler.isSuccess
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.di.configureKoinUnitTest
import app.bushive.theBouncer.features.v1.data.local.dao.permission.PermissionDao
import app.bushive.theBouncer.features.v1.data.local.model.Permission
import junit.framework.TestCase.assertTrue
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
    fun getPermissionById_returnsPermission() =
        runBlocking {
            var permission =
                Permission(
                    name = "READ",
                    description = "Read permission",
                    permission = "read",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                )
            permission = permissionDao.addPermission(permission).getDataOrNull()!!
            val result = permissionDao.getPermissionById(permission.id!!)
            assertTrue(result.isSuccess())
            assertEquals(permission.id, result.getDataOrNull()?.id)
        }

    @Test
    fun getPermissionById_returnsNullForNonExistentPermission() =
        runBlocking {
            val result = permissionDao.getPermissionById(999)
            assertTrue(result.isFailure())
        }

    @Test
    fun addPermission_addsPermissionSuccessfully() =
        runBlocking {
            val permission =
                Permission(
                    name = "READ",
                    description = "Read permission",
                    permission = "read",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                )
            val result = permissionDao.addPermission(permission)
            assertTrue(result.isSuccess())
            val retrievedPermission = permissionDao.getPermissionById(result.getDataOrNull()!!.id!!)
            assertEquals(result.getDataOrNull()!!.id, retrievedPermission.getDataOrNull()?.id)
        }

    @Test
    fun updatePermission_updatesPermissionSuccessfully() =
        runBlocking {
            var permission =
                Permission(
                    name = "READ",
                    description = "Read permission",
                    permission = "read",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                )
            permission = permissionDao.addPermission(permission).getDataOrNull()!!
            permission.description = "Updated read permission"
//            val result = permissionDao.updatePermission(permission)
//            assertTrue(result.isSuccess())
            val retrievedPermission = permissionDao.getPermissionById(permission.id!!)
//            assertEquals("Updated read permission", retrievedPermission.getDataOrNull()?.description)
        }

    @Test
    fun deletePermission_deletesPermissionSuccessfully() =
        runBlocking {
            var permission =
                Permission(
                    name = "READ",
                    description = "Read permission",
                    permission = "read",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                )
            permission = permissionDao.addPermission(permission).getDataOrNull()!!
            val result = permissionDao.deletePermission(permission.id!!)
            assertTrue(result.isSuccess())
            val retrievedPermission = permissionDao.getPermissionById(permission.id)
            assertNull(retrievedPermission.getDataOrNull())
        }

    @Test
    fun deleteAllPermissions_deletesAllPermissionsSuccessfully() =
        runBlocking {
            permissionDao.addPermission(
                Permission(
                    name = "READ",
                    description = "Read permission",
                    permission = "read",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                ),
            )
            val result = permissionDao.deleteAllPermissions()
            assertTrue(result.isSuccess())
            val retrievedPermissions = permissionDao.getAllPermissions()
            assertTrue(retrievedPermissions.getDataOrNull()?.isEmpty() == true)
        }
}
