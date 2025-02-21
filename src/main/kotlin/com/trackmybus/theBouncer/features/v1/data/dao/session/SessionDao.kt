package com.trackmybus.theBouncer.features.v1.data.dao.session

import com.trackmybus.theBouncer.features.v1.data.entity.SessionEntity
import com.trackmybus.theBouncer.features.v1.data.model.Session
import java.util.UUID

interface SessionDao {
    suspend fun getAllSessions(): Result<List<SessionEntity>>

    suspend fun getSessionById(id: UUID): Result<SessionEntity?>

    suspend fun addSession(session: Session): Result<UUID>

    suspend fun addSessions(sessions: List<Session>): Result<Unit>

    suspend fun updateSession(session: Session): Result<Unit>

    suspend fun deleteSession(id: UUID): Result<Unit>

    suspend fun deleteAllSessions(): Result<Unit>
}
