package app.bushive.theBouncer.features.v1.data.local.dao

import app.bushive.theBouncer.core.result.ResultHandler.isFailure
import app.bushive.theBouncer.core.result.ResultHandler.isSuccess
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.di.configureKoinUnitTest
import app.bushive.theBouncer.features.v1.data.local.dao.permission.PermissionDao
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroup.PermissionGroupDao
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroup
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

class PermissionGroupDaoTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var permissionDao: PermissionDao
    private lateinit var permissionGroupDao: PermissionGroupDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
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
    fun addPermissionGroup_addsPermissionGroupSuccessfully() =
        runBlocking {
            val permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            val result = permissionGroupDao.addPermissionGroup(permissionGroup)
            assertTrue(result.isSuccess())
            assertEquals("Admin", result.getDataOrNull()?.name)
        }

    @Test
    fun getPermissionGroup_withValidId_returnsPermissionGroup() =
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
            val result = permissionGroupDao.getPermissionGroup(permissionGroup?.id!!)
            assertTrue(result.isSuccess())
            assertEquals("Admin", result.getDataOrNull()?.name)
        }

    @Test
    fun getPermissionGroup_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionGroupDao.getPermissionGroup(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun getPermissionGroups_returnsAllPermissionGroups() =
        runBlocking {
            permissionGroupDao.addPermissionGroup(
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                ),
            )
            permissionGroupDao.addPermissionGroup(
                PermissionGroup(
                    name = "Admin",
                    description = "",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                ),
            )
            val result = permissionGroupDao.getPermissionGroups()
            assertTrue(result.isSuccess())
            assertEquals(2, result.getDataOrNull()?.size)
        }

    @Test
    fun updatePermissionGroup_updatesPermissionGroupSuccessfully() =
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
            val updatedPermissionGroup = permissionGroup?.copy(name = "SuperAdmin")
            val result = permissionGroupDao.updatePermissionGroup(updatedPermissionGroup!!)
            assertTrue(result.isSuccess())
            assertEquals("SuperAdmin", result.getDataOrNull()?.name)
        }

    @Test
    fun deletePermissionGroup_withValidId_deletesPermissionGroupSuccessfully() =
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
            val result = permissionGroupDao.deletePermissionGroup(permissionGroup?.id!!)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deletePermissionGroup_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionGroupDao.deletePermissionGroup(-1)
            assertTrue(result.isFailure())
        }
}
