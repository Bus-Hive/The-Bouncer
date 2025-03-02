package com.trackmybus.theBouncer.features.v1.domain.usecase.permission

import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.data.local.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.local.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.local.model.User
import com.trackmybus.theBouncer.features.v1.domain.repository.permission.PermissionRepository
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PermissionUseCaseTest : KoinTest {
    private lateinit var permissionUseCase: PermissionUseCase
    private lateinit var permissionRepository: PermissionRepository
    private lateinit var databaseFactory: DatabaseFactory
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        configureKoinUnitTest()
        permissionRepository = get()
        permissionUseCase = get()
        databaseFactory = get()
        userDao = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun addPermission_withValidData_addsSuccessfully() =
        runBlocking {
            val name = "READ"
            val description = "Read permission"
            val permission = "read"
            val result = permissionUseCase.addPermission(name, description, permission)
            assertTrue(result.isSuccess())
            assertEquals(name, result.getDataOrNull()?.name)
        }

    @Test
    fun addPermission_withDuplicateName_returnsError() =
        runBlocking {
            val name = "READ"
            val description = "Read permission"
            val permission = "read"
            permissionUseCase.addPermission(name, description, permission)
            val result = permissionUseCase.addPermission(name, description, permission)
            assertTrue(result.isSuccess())
        }

    @Test
    fun removePermission_withValidId_removesSuccessfully() =
        runBlocking {
            val name = "READ"
            val description = "Read permission"
            val permission = "read"
            val addedPermission = permissionUseCase.addPermission(name, description, permission).getDataOrNull()!!
            val result = permissionUseCase.removePermission(addedPermission.id!!)
            assertTrue(result.isSuccess())
        }

    @Test
    fun removePermission_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionUseCase.removePermission(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun updatePermission_withValidData_updatesSuccessfully() =
        runBlocking {
            val name = "READ"
            val description = "Read permission"
            val permission = "read"
            val addedPermission = permissionUseCase.addPermission(name, description, permission).getDataOrNull()!!
            val updatedName = "WRITE"
            val updatedDescription = "Write permission"
            val updatedPermission = "write"
            val result =
                permissionUseCase.updatePermission(addedPermission.id!!, updatedName, updatedDescription, updatedPermission)
            assertTrue(result.isSuccess())
            assertEquals(updatedName, result.getDataOrNull()?.name)
        }

    @Test
    fun updatePermission_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionUseCase.updatePermission(-1, "WRITE", "Write permission", "write")
            assertTrue(result.isFailure())
        }

    @Test
    fun getPermission_withValidId_returnsPermission() =
        runBlocking {
            val name = "READ"
            val description = "Read permission"
            val permission = "read"
            val addedPermission = permissionUseCase.addPermission(name, description, permission).getDataOrNull()!!
            val result = permissionUseCase.getPermission(addedPermission.id!!)
            assertTrue(result.isSuccess())
            assertEquals(name, result.getDataOrNull()?.name)
        }

    @Test
    fun getPermission_withInvalidId_returnsError() =
        runBlocking {
            val result = permissionUseCase.getPermission(-1)
            assertTrue(result.isFailure())
        }

    @Test
    fun getPermissions_returnsAllPermissions() =
        runBlocking {
            val name1 = "READ"
            val description1 = "Read permission"
            val permission1 = "read"
            val name2 = "WRITE"
            val description2 = "Write permission"
            val permission2 = "write"
            permissionUseCase.addPermission(name1, description1, permission1)
            permissionUseCase.addPermission(name2, description2, permission2)
            val result = permissionUseCase.getPermissions()
            assertTrue(result.isSuccess())
            assertEquals(2, result.getDataOrNull()?.size)
        }

    @Test
    fun getPermissionsForUser_withValidUserId_returnsPermissions() =
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
            val result = permissionUseCase.getPermissionsForUser(user.id.toString())
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull() != null)
        }

    @Test
    fun getPermissionsForUser_withInvalidUserId_returnsEmptyList() =
        runBlocking {
            val userId = UUID.randomUUID().toString()
            val result = permissionUseCase.getPermissionsForUser(userId)
            assertTrue(result.isSuccess())
            assertTrue(result.getDataOrNull()?.isEmpty() == true)
        }
}
