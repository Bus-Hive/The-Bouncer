package app.bushive.theBouncer.features.v1.domain.usecase.permissionGroup

import app.bushive.theBouncer.core.result.ResultHandler.isFailure
import app.bushive.theBouncer.core.result.ResultHandler.isSuccess
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.di.configureKoinUnitTest
import app.bushive.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PermissionGroupUseCaseTest : KoinTest {
    private lateinit var permissionGroupUseCase: PermissionGroupUseCase
    private lateinit var permissionGroupRepository: PermissionGroupRepository
    private lateinit var databaseFactory: DatabaseFactory

    @Before
    fun setup() {
        configureKoinUnitTest()
        permissionGroupRepository = get()
        permissionGroupUseCase = get()
        databaseFactory = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun addPermissionGroup_withValidData_addsSuccessfully() =
        runBlocking {
            val name = "Admin"
            val description = "Admin group"
            val isBaseUserGroup = true
            val result = permissionGroupUseCase.addPermissionGroup(name, description, isBaseUserGroup)
            assertTrue(result.isSuccess())
            assertEquals(name, result.getDataOrNull()?.name)
        }

    @Test
    fun addPermissionGroup_withDuplicateName_returnsError() =
        runBlocking {
            val name = "Admin"
            val description = "Admin group"
            val isBaseUserGroup = true
            permissionGroupUseCase.addPermissionGroup(name, description, isBaseUserGroup)
            val result = permissionGroupUseCase.addPermissionGroup(name, description, isBaseUserGroup)
            assertTrue(result.isSuccess())
        }

    @Test
    fun removePermissionGroup_withValidId_removesSuccessfully() =
        runBlocking {
            val name = "Admin"
            val description = "Admin group"
            val isBaseUserGroup = true
            val addedGroup = permissionGroupUseCase.addPermissionGroup(name, description, isBaseUserGroup).getDataOrNull()!!
            val result = permissionGroupUseCase.removePermissionGroup(addedGroup.id)
            assertTrue(result.isSuccess())
        }

    @Test
    fun removePermissionGroup_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionGroupUseCase.removePermissionGroup(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun updatePermissionGroup_withValidData_updatesSuccessfully() =
        runBlocking {
            val name = "Admin"
            val description = "Admin group"
            val isBaseUserGroup = true
            val addedGroup = permissionGroupUseCase.addPermissionGroup(name, description, isBaseUserGroup).getDataOrNull()!!
            val updatedName = "SuperAdmin"
            val updatedDescription = "Super Admin group"
            val result =
                permissionGroupUseCase.updatePermissionGroup(
                    addedGroup.id,
                    updatedName,
                    updatedDescription,
                    isBaseUserGroup,
                )
            assertTrue(result.isSuccess())
            assertEquals(updatedName, result.getDataOrNull()?.name)
        }

    @Test
    fun updatePermissionGroup_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionGroupUseCase.updatePermissionGroup(-1, "SuperAdmin", "Super Admin group", true)
            assertTrue(result.isFailure())
        }

    @Test
    fun getPermissionGroup_withValidId_returnsPermissionGroup() =
        runBlocking {
            val name = "Admin"
            val description = "Admin group"
            val isBaseUserGroup = true
            val addedGroup = permissionGroupUseCase.addPermissionGroup(name, description, isBaseUserGroup).getDataOrNull()!!
            val result = permissionGroupUseCase.getPermissionGroup(addedGroup.id)
            assertTrue(result.isSuccess())
            assertEquals(name, result.getDataOrNull()?.name)
        }

    @Test
    fun getPermissionGroup_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionGroupUseCase.getPermissionGroup(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun getPermissionGroups_returnsAllPermissionGroups() =
        runBlocking {
            val name1 = "Admin"
            val description1 = "Admin group"
            val isBaseUserGroup1 = true
            val name2 = "User"
            val description2 = "User group"
            val isBaseUserGroup2 = false
            permissionGroupUseCase.addPermissionGroup(name1, description1, isBaseUserGroup1)
            permissionGroupUseCase.addPermissionGroup(name2, description2, isBaseUserGroup2)
            val result = permissionGroupUseCase.getPermissionGroups()
            assertTrue(result.isSuccess())
            assertEquals(2, result.getDataOrNull()?.size)
        }

    @Test
    fun getBasePermissionGroups_returnsBasePermissionGroups() =
        runBlocking {
            val name1 = "Admin"
            val description1 = "Admin group"
            val isBaseUserGroup1 = true
            val name2 = "User"
            val description2 = "User group"
            val isBaseUserGroup2 = false
            permissionGroupUseCase.addPermissionGroup(name1, description1, isBaseUserGroup1)
            permissionGroupUseCase.addPermissionGroup(name2, description2, isBaseUserGroup2)
            val result = permissionGroupUseCase.getBasePermissionGroups()
            assertTrue(result.isSuccess())
            assertEquals(1, result.getDataOrNull()?.size)
        }
}
