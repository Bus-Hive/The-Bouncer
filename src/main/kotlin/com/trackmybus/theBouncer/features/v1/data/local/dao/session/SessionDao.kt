package com.trackmybus.theBouncer.features.v1.data.local.dao.session

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.local.model.Session
import java.util.UUID

interface SessionDao {
    suspend fun getAllSessions(): Result<List<Session>, RootError>

    suspend fun getSessionById(id: UUID): Result<Session, RootError>

    suspend fun addSession(session: Session): Result<Session, RootError>

    suspend fun addSessions(sessions: List<Session>): Result<Unit, RootError>

    suspend fun updateSession(session: Session): Result<Session, RootError>

    suspend fun deleteSession(id: UUID): Result<Unit, RootError>

    suspend fun deleteAllSessions(): Result<Unit, RootError>
}
