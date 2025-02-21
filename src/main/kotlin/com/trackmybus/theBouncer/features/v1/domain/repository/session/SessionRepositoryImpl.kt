package com.trackmybus.theBouncer.features.v1.domain.repository.session

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.features.v1.data.dao.session.SessionDao
import com.trackmybus.theBouncer.features.v1.data.mapper.SessionEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.model.Session
import io.ktor.util.logging.Logger
import java.util.UUID

class SessionRepositoryImpl(
    private val logger: Logger,
    private val sessionDao: SessionDao,
) : SessionRepository {
    override suspend fun getAll(): Result<List<Session>> {
        logger.info("Fetching all sessions")
        return sessionDao
            .getAllSessions()
            .mapResult { it.toModel() }
            .also { result ->
                result.onSuccess { logger.info("Successfully fetched all sessions") }
                result.onFailure { logger.error("Error fetching all sessions", it) }
            }
    }

    override suspend fun getById(id: UUID): Result<Session?> {
        logger.info("Fetching session with id: $id")
        return sessionDao
            .getSessionById(id)
            .mapResult { it?.toModel() }
            .also { result ->
                result.onSuccess { logger.info("Successfully fetched session with id: $id") }
                result.onFailure { logger.error("Error fetching session with id: $id", it) }
            }
    }

    override suspend fun add(session: Session): Result<UUID> {
        logger.info("Adding session: ${session.sessionID}")
        return sessionDao
            .addSession(session)
            .also { result ->
                result.onSuccess { logger.info("Successfully added session: ${result.getOrNull()}") }
                result.onFailure { logger.error("Error adding session: ${result.getOrNull()}", it) }
            }
    }

    override suspend fun add(sessions: List<Session>): Result<Unit> {
        logger.info("Adding sessions: ${sessions.joinToString { it.sessionID.toString() }}")
        return sessionDao
            .addSessions(sessions)
            .also { result ->
                result.onSuccess { logger.info("Successfully added sessions: ${sessions.joinToString { it.sessionID.toString() }}") }
                result.onFailure {
                    logger.error(
                        "Error adding sessions: ${sessions.joinToString { it.sessionID.toString() }}",
                        it,
                    )
                }
            }
    }

    override suspend fun update(session: Session): Result<Unit> =
        sessionDao
            .updateSession(session)
            .also { result ->
                result.onSuccess { logger.info("Successfully updated session: ${session.sessionID}") }
                result.onFailure { logger.error("Error updating session: ${session.sessionID}", it) }
            }

    override suspend fun deleteById(id: UUID): Result<Unit> =
        sessionDao
            .deleteSession(id)
            .also { result ->
                result.onSuccess { logger.info("Successfully deleted session with id: $id") }
                result.onFailure { logger.error("Error deleting session with id: $id", it) }
            }

    override suspend fun deleteAll(): Result<Unit> =
        sessionDao
            .deleteAllSessions()
            .also { result ->
                result.onSuccess { logger.info("Successfully deleted all sessions") }
                result.onFailure { logger.error("Error deleting all sessions", it) }
            }
}
