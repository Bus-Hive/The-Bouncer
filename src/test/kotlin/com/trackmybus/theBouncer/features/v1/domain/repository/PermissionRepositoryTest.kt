import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.local.dao.permission.PermissionDao
import com.trackmybus.theBouncer.features.v1.data.local.dao.permissionGroup.PermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.local.dao.permissionGroupPermission.PermissionGroupPermissionDao
import com.trackmybus.theBouncer.features.v1.data.local.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.local.dao.userPermissionGroup.UserPermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.local.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.local.model.Permission
import com.trackmybus.theBouncer.features.v1.data.local.model.PermissionGroup
import com.trackmybus.theBouncer.features.v1.data.local.model.User
import com.trackmybus.theBouncer.features.v1.data.local.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.domain.repository.permission.PermissionRepository
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

class PermissionRepositoryTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var permissionRepository: PermissionRepository
    private lateinit var permissionDao: PermissionDao
    private lateinit var userPermissionGroupDao: UserPermissionGroupDao
    private lateinit var permissionGroupDao: PermissionGroupDao
    private lateinit var userDao: UserDao
    private lateinit var permissionGroupPermissionRepository: PermissionGroupPermissionDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        permissionDao = get()
        userPermissionGroupDao = get()
        permissionGroupDao = get()
        permissionGroupPermissionRepository = get()
        permissionRepository = get()
        userDao = get()
        databaseFactory = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun getAll_returnsAllPermissions() =
        runBlocking {
            val result = permissionRepository.getAll()
            assertTrue(result.isSuccess())
        }

    @Test
    fun getById_withValidId_returnsPermission() =
        runBlocking {
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    ).getDataOrNull()!!
            val result = permissionRepository.getById(permission.id!!)
            assertTrue(result.isSuccess())
            assertEquals(permission.id, result.getDataOrNull()?.id)
        }

    @Test
    fun getById_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionRepository.getById(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun add_withValidPermission_addsSuccessfully() =
        runBlocking {
            val permission =
                Permission(
                    name = "READ",
                    description = "Read permission",
                    permission = "read",
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                )
            val result = permissionRepository.add(permission)
            assertTrue(result.isSuccess())
        }

    @Test
    fun update_withValidPermission_updatesSuccessfully() =
        runBlocking {
            var permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    ).getDataOrNull()!!
            permission = permission.copy(description = "Updated read permission")
            val result = permissionRepository.update(permission)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deleteById_withValidId_deletesSuccessfully() =
        runBlocking {
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    ).getDataOrNull()!!
            val result = permissionRepository.deleteById(permission.id!!)
            assertTrue(result.isSuccess())
        }

    @Test
    fun deleteById_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionRepository.deleteById(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun deleteAll_deletesAllPermissionsSuccessfully() =
        runBlocking {
            val result = permissionRepository.deleteAll()
            assertTrue(result.isSuccess())
        }

    @Test
    fun getPermissionsByUserId_withValidUserId_returnsPermissions() =
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
                    ).getDataOrNull()!!
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
                    ).getDataOrNull()!!
            userPermissionGroupDao.addUserPermissionGroup(
                UserPermissionGroup(userId = user.id, permissionGroupId = permissionGroup.id),
            )
            val permission =
                permissionDao
                    .addPermission(
                        Permission(
                            name = "READ",
                            description = "Read permission",
                            permission = "read",
                            createdAt = Clock.System.now().toLocalDateTime(UTC),
                        ),
                    ).getDataOrNull()!!
            permissionGroupPermissionRepository.addPermissionGroupPermission(permissionGroup.id, permission.id!!)
            val result = permissionRepository.getPermissionsByUserId(user.id!!)
            assertTrue(result.isSuccess())
            assertEquals(1, result.getDataOrNull()?.size)
        }

    @Test
    fun getPermissionsByUserId_withInvalidUserId_returnsEmptyList() =
        runBlocking {
            val result = permissionRepository.getPermissionsByUserId(UUID.randomUUID())
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull()?.isEmpty() ?: false)
        }
}
