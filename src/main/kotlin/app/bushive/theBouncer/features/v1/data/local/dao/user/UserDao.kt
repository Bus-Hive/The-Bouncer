package app.bushive.theBouncer.features.v1.data.local.dao.user

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.User
import java.util.UUID

interface UserDao {
    suspend fun getAllUsers(): Result<List<User>, RootError>

    suspend fun getUserById(id: UUID): Result<User, RootError>

    suspend fun getUserByEmail(email: String): Result<User, RootError>

    suspend fun addUser(user: User): Result<User, RootError>

    suspend fun addUsers(users: List<User>): Result<Unit, RootError>

    suspend fun updateUser(user: User): Result<User, RootError>

    suspend fun deleteUser(id: UUID): Result<Unit, RootError>

    suspend fun deleteAllUsers(): Result<Unit, RootError>

    suspend fun isEmailUnique(email: String): Result<Boolean, RootError>
}
