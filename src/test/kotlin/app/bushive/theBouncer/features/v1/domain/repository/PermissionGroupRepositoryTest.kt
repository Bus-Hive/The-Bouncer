import app.bushive.theBouncer.core.result.ResultHandler.isFailure
import app.bushive.theBouncer.core.result.ResultHandler.isSuccess
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.di.configureKoinUnitTest
import app.bushive.theBouncer.features.v1.data.local.dao.permission.PermissionDao
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroup.PermissionGroupDao
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroupPermission.PermissionGroupPermissionDao
import app.bushive.theBouncer.features.v1.data.local.dao.user.UserDao
import app.bushive.theBouncer.features.v1.data.local.dao.userPermissionGroup.UserPermissionGroupDao
import app.bushive.theBouncer.features.v1.data.local.model.AuthProvider
import app.bushive.theBouncer.features.v1.data.local.model.Permission
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroup
import app.bushive.theBouncer.features.v1.data.local.model.User
import app.bushive.theBouncer.features.v1.data.local.model.UserPermissionGroup
import app.bushive.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepository
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PermissionGroupRepositoryTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var permissionGroupRepository: PermissionGroupRepository
    private lateinit var permissionGroupDao: PermissionGroupDao
    private lateinit var permissionGroupPermissionDao: PermissionGroupPermissionDao
    private lateinit var userDao: UserDao
    private lateinit var permissionDao: PermissionDao
    private lateinit var userPermissionGroupDao: UserPermissionGroupDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        permissionGroupDao = get()
        permissionGroupPermissionDao = get()
        userPermissionGroupDao = get()
        permissionGroupRepository = get()
        databaseFactory = get()
        userDao = get()
        permissionDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun addPermissionGroup_addsSuccessfully() =
        runBlocking {
            val permissionGroup =
                PermissionGroup(
                    name = "Admin",
                    description = "Admin group",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                )
            val result = permissionGroupRepository.addPermissionGroup(permissionGroup)
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
                            description = "Admin group",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val result = permissionGroupRepository.getPermissionGroup(permissionGroup?.id!!)
            assertTrue(result.isSuccess())
            assertEquals("Admin", result.getDataOrNull()?.name)
        }

    @Test
    fun getPermissionGroup_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionGroupRepository.getPermissionGroup(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun getPermissionGroups_returnsAllPermissionGroups() =
        runBlocking {
            permissionGroupDao.addPermissionGroup(
                PermissionGroup(
                    name = "Admin",
                    description = "Admin group",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = true,
                    permissions = emptyList(),
                ),
            )
            permissionGroupDao.addPermissionGroup(
                PermissionGroup(
                    name = "User",
                    description = "User group",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                    isBaseUserGroup = false,
                    permissions = emptyList(),
                ),
            )
            val result = permissionGroupRepository.getPermissionGroups()
            assertTrue(result.isSuccess())
            assertEquals(2, result.getDataOrNull()?.size)
        }

    @Test
    fun updatePermissionGroup_updatesSuccessfully() =
        runBlocking {
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "Admin group",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val updatedPermissionGroup = permissionGroup?.copy(name = "SuperAdmin")
            val result = permissionGroupRepository.updatePermissionGroup(updatedPermissionGroup!!)
            assertTrue(result.isSuccess())
            assertEquals("SuperAdmin", result.getDataOrNull()?.name)
        }

    @Test
    fun deletePermissionGroup_withValidId_deletesSuccessfully() =
        runBlocking {
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "Admin group",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val result = permissionGroupRepository.deletePermissionGroup(permissionGroup?.id!!)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deletePermissionGroup_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionGroupRepository.deletePermissionGroup(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun getPermissionGroupsByUserId_withValidUserId_returnsPermissionGroups() =
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
                            description = "Admin group",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            userPermissionGroupDao.addUserPermissionGroup(
                UserPermissionGroup(userId = user?.id, permissionGroupId = permissionGroup?.id),
            )
            val result = permissionGroupRepository.getPermissionGroupsByUserId(user?.id!!)
            assertTrue(result.isSuccess())
            assertEquals(1, result.getDataOrNull()?.size)
        }

    @Test
    fun getPermissionGroupsByUserId_withInvalidUserId_returnsEmptyList() =
        runBlocking {
            val result = permissionGroupRepository.getPermissionGroupsByUserId(UUID.randomUUID())
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull()?.isEmpty() ?: false)
        }

    @Test
    fun addPermissionToGroup_addsSuccessfully() =
        runBlocking {
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "Admin group",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    ).getDataOrNull()
            val result = permissionGroupRepository.addPermissionToGroup(permissionGroup?.id!!, permission?.id!!)
            assertTrue(result.isSuccess())
        }

    @Test
    fun removePermissionFromGroup_removesSuccessfully() =
        runBlocking {
            val permissionGroup =
                permissionGroupDao
                    .addPermissionGroup(
                        PermissionGroup(
                            name = "Admin",
                            description = "Admin group",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                            isBaseUserGroup = true,
                            permissions = emptyList(),
                        ),
                    ).getDataOrNull()
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    ).getDataOrNull()
            permissionGroupPermissionDao.addPermissionGroupPermission(permissionGroup?.id!!, permission?.id!!)
            val result = permissionGroupRepository.removePermissionFromGroup(permissionGroup?.id!!, permission?.id!!)
            assertTrue(result.isSuccess())
        }
}
