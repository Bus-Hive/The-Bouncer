package com.trackmybus.theBouncer.features.v1.data.dao.session

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.SessionEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.SessionEntityMapper.toEntities
import com.trackmybus.theBouncer.features.v1.data.mapper.SessionEntityMapper.toEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.SessionEntityMapper.updateModel
import com.trackmybus.theBouncer.features.v1.data.model.Session
import io.ktor.util.logging.Logger
import java.util.UUID

class SessionDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : SessionDao {
    override suspend fun getAllSessions(): Result<List<SessionEntity>> =
        runCatching {
            dbFactory.dbQuery {
                SessionEntity.all().toList()
            }
        }.onFailure {
            logger.error("Error getting all sessions", it)
        }

    override suspend fun getSessionById(id: UUID): Result<SessionEntity?> =
        runCatching {
            dbFactory.dbQuery {
                SessionEntity.findById(id)
            }
        }.onFailure {
            logger.error("Error getting session by id: $id", it)
        }

    override suspend fun addSession(session: Session): Result<UUID> =
        runCatching {
            session.toEntity()
        }.onFailure {
            logger.error(
                "Error adding session: ${session.sessionID}, ${session.userId}, ${session.refreshToken}, ${session.expiresAt}",
                it,
            )
        }

    override suspend fun addSessions(sessions: List<Session>): Result<Unit> =
        runCatching {
            sessions.toEntities()
        }.onFailure {
            logger.error("Error adding sessions: $sessions", it)
        }

    override suspend fun updateSession(session: Session): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                session.sessionID?.let {
                    SessionEntity.findById(it).apply {
                        this?.updateModel(session)
                    }
                }
            }
            Unit
        }.onFailure {
            logger.error(
                "Error updating session: ${session.sessionID}, ${session.userId}, ${session.refreshToken}, ${session.expiresAt}",
                it,
            )
        }

    override suspend fun deleteSession(id: UUID): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                SessionEntity.findById(id)?.delete()
            }
            Unit
        }.onFailure {
            logger.error("Error deleting session by id: $id", it)
        }

    override suspend fun deleteAllSessions(): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                SessionEntity.all().forEach { it.delete() }
            }
        }.onFailure {
            logger.error("Error deleting all sessions", it)
        }
}
