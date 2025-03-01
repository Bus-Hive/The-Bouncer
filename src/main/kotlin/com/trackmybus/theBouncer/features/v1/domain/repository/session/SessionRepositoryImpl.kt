package com.trackmybus.theBouncer.features.v1.domain.repository.session

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.onSuccess
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.dao.session.SessionDao
import com.trackmybus.theBouncer.features.v1.data.model.Session
import io.ktor.util.logging.Logger
import java.util.UUID

class SessionRepositoryImpl(
    private val logger: Logger,
    private val sessionDao: SessionDao,
) : SessionRepository {
    override suspend fun getAll(): Result<List<Session>, RootError> {
        logger.info("Fetching all sessions")
        return sessionDao
            .getAllSessions()
            .also { result ->
                result.onSuccess { logger.info("Successfully fetched all sessions") }
                result.onFailure { logger.error("Error fetching all sessions") }
            }
    }

    override suspend fun getById(id: UUID): Result<Session, RootError> {
        logger.info("Fetching session with id: $id")
        return sessionDao
            .getSessionById(id)
            .also { result ->
                result.onSuccess { logger.info("Successfully fetched session with id: $id") }
                result.onFailure { logger.error("Error fetching session with id: $id") }
            }
    }

    override suspend fun add(session: Session): Result<Session, RootError> {
        logger.info("Adding session: ${session.sessionID}")
        return sessionDao
            .addSession(session)
            .also { result ->
                result.onSuccess { logger.info("Successfully added session: ${result.getDataOrNull()}") }
                result.onFailure { logger.error("Error adding session: ${result.getDataOrNull()}") }
            }
    }

    override suspend fun add(sessions: List<Session>): Result<Unit, RootError> {
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

    override suspend fun update(session: Session): Result<Session, RootError> =
        sessionDao
            .updateSession(session)
            .also { result ->
                result.onSuccess { logger.info("Successfully updated session: ${session.sessionID}") }
                result.onFailure { logger.error("Error updating session: ${session.sessionID}") }
            }

    override suspend fun deleteById(id: UUID): Result<Unit, RootError> =
        sessionDao
            .deleteSession(id)
            .also { result ->
                result.onSuccess { logger.info("Successfully deleted session with id: $id") }
                result.onFailure { logger.error("Error deleting session with id: $id") }
            }

    override suspend fun deleteAll(): Result<Unit, RootError> =
        sessionDao
            .deleteAllSessions()
            .also { result ->
                result.onSuccess { logger.info("Successfully deleted all sessions") }
                result.onFailure { logger.error("Error deleting all sessions") }
            }
}
