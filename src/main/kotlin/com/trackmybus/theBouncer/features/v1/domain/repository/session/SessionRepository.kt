package com.trackmybus.theBouncer.features.v1.domain.repository.session

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.local.model.Session
import java.util.UUID

interface SessionRepository {
    suspend fun getAll(): Result<List<Session>, RootError>

    suspend fun getById(id: UUID): Result<Session, RootError>

    suspend fun add(session: Session): Result<Session, RootError>

    suspend fun add(sessions: List<Session>): Result<Unit, RootError>

    suspend fun update(session: Session): Result<Session, RootError>

    suspend fun deleteById(id: UUID): Result<Unit, RootError>

    suspend fun deleteAll(): Result<Unit, RootError>
}
