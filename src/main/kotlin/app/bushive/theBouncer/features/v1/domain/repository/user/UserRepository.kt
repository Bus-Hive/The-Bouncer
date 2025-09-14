package app.bushive.theBouncer.features.v1.domain.repository.user

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.User
import java.util.UUID

interface UserRepository {
    suspend fun getAll(): Result<List<User>, RootError>

    suspend fun getById(id: UUID): Result<User, RootError>

    suspend fun getByEmail(email: String): Result<User, RootError>

    suspend fun isEmailUnique(email: String): Result<Boolean, RootError>

    suspend fun add(user: User): Result<User, RootError>

    suspend fun add(users: List<User>): Result<Unit, RootError>

    suspend fun update(user: User): Result<User, RootError>

    suspend fun delete(id: UUID): Result<Unit, RootError>

    suspend fun deleteAll(): Result<Unit, RootError>
}
