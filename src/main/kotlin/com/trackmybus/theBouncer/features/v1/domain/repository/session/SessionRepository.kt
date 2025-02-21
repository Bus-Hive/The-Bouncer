package com.trackmybus.theBouncer.features.v1.domain.repository.session

import com.trackmybus.theBouncer.features.v1.data.model.Session
import java.util.UUID

interface SessionRepository {
    suspend fun getAll(): Result<List<Session>>

    suspend fun getById(id: UUID): Result<Session?>

    suspend fun add(session: Session): Result<UUID>

    suspend fun add(sessions: List<Session>): Result<Unit>

    suspend fun update(session: Session): Result<Unit>

    suspend fun deleteById(id: UUID): Result<Unit>

    suspend fun deleteAll(): Result<Unit>
}
