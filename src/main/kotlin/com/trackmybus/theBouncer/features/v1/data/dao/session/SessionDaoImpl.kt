package com.trackmybus.theBouncer.features.v1.data.dao.session

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.addMessage
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.ValidationError
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.SessionEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.SessionEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.model.Session
import com.trackmybus.theBouncer.features.v1.data.tables.SessionsTable
import com.trackmybus.theBouncer.features.v1.data.tables.UsersTable
import io.ktor.util.logging.Logger
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class SessionDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : SessionDao {
    override suspend fun getAllSessions(): Result<List<Session>, RootError> =
        dbFactory
            .dbQuery {
                SessionEntity.all().toList().toModel()
            }.addMessage(
                success = "Successfully fetched all sessions",
                failure = "Error fetching all sessions",
            ).onFailure {
                logger.error("Error getting all sessions")
            }

    override suspend fun getSessionById(id: UUID): Result<Session, RootError> =
        dbFactory
            .dbQuery {
                SessionEntity.findById(id)?.toModel()
            }.addMessage(
                success = "Successfully fetched session with id: $id",
                failure = "Error fetching session with id: $id",
            ).onFailure {
                logger.error("Error getting session by id: $id")
            }

    override suspend fun addSession(session: Session): Result<Session, RootError> =
        dbFactory
            .dbQuery {
                require(session.sessionID != null && session.userId != null && session.refreshToken != null && session.expiresAt != null) {
                    Result.Error(error = ValidationError.EmptyField, data = session)
                }

                SessionEntity
                    .new {
                        sessionId = EntityID(session.sessionID, SessionsTable)
                        userId = EntityID(session.userId, UsersTable)
                        token = session.refreshToken
                        expiry = session.expiresAt
                    }.toModel()
            }.addMessage(
                success = "Session added successfully",
                failure = "Failed to add session",
            ).onFailure {
                logger.error(
                    "Error adding session: ${session.sessionID}, ${session.userId}, ${session.refreshToken}, ${session.expiresAt}",
                    it,
                )
            }

    override suspend fun addSessions(sessions: List<Session>): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                sessions.forEach { session ->
                    require(
                        session.sessionID != null && session.userId != null && session.refreshToken != null && session.expiresAt != null,
                    ) {
                        Result.Error(error = ValidationError.EmptyField, data = session)
                    }
                    SessionEntity
                        .new {
                            sessionId = EntityID(session.sessionID, SessionsTable)
                            userId = EntityID(session.userId, UsersTable)
                            token = session.refreshToken
                            expiry = session.expiresAt
                        }
                }
            }.addMessage(
                success = "Sessions added successfully",
                failure = "Failed to add sessions",
            ).onFailure {
                logger.error("Error adding sessions: $sessions")
            }

    override suspend fun updateSession(session: Session): Result<Session, RootError> =
        dbFactory
            .dbQuery {
                session.sessionID?.let {
                    SessionEntity
                        .findByIdAndUpdate(it) { entity ->
                            require(
                                session.userId != null && session.refreshToken != null && session.expiresAt != null,
                            ) {
                                Result.Error(error = ValidationError.EmptyField, data = session)
                            }
                            entity.userId = EntityID(session.userId, UsersTable)
                            entity.token = session.refreshToken
                            entity.expiry = session.expiresAt
                        }?.toModel()
                }
            }.addMessage(
                success = "Session updated successfully",
                failure = "Failed to update session",
            ).onFailure {
                logger.error(
                    "Error updating session: ${session.sessionID}, ${session.userId}, ${session.refreshToken}, ${session.expiresAt}",
                    it,
                )
            }

    override suspend fun deleteSession(id: UUID): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                SessionEntity.findById(id)?.delete()
            }.addMessage(
                success = "Session deleted successfully",
                failure = "Failed to delete session",
            ).onFailure {
                logger.error("Error deleting session by id: $id")
            }

    override suspend fun deleteAllSessions(): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                SessionEntity.all().forEach { it.delete() }
            }.addMessage(
                success = "All sessions deleted successfully",
                failure = "Failed to delete all sessions",
            ).onFailure {
                logger.error("Error deleting all sessions")
            }
}
