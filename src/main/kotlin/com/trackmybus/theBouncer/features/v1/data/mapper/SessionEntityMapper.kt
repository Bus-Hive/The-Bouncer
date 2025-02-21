package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.SessionEntity
import com.trackmybus.theBouncer.features.v1.data.model.Session
import com.trackmybus.theBouncer.features.v1.data.tables.SessionsTable
import com.trackmybus.theBouncer.features.v1.data.tables.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

object SessionEntityMapper : KoinComponent {
    private val dbFactory: DatabaseFactory by inject()

    suspend fun Session.toEntity(): UUID {
        require(sessionID != null) {
            "Cannot create entity with null sessionID"
        }

        require(userId != null) {
            "Cannot create entity with null userId"
        }

        require(refreshToken != null) {
            "Cannot create entity with null refreshToken"
        }

        require(expiresAt != null) {
            "Cannot create entity with null expiresAt"
        }

        return dbFactory.dbQuery {
            val session =
                SessionEntity.new {
                    this.sessionId = EntityID(this@toEntity.sessionID, SessionsTable)
                    this.userId = EntityID(this@toEntity.userId, UsersTable)
                    this.token = this@toEntity.refreshToken
                    this.expiry = this@toEntity.expiresAt
                }
            return@dbQuery session.sessionId.value
        }
    }

    suspend fun List<Session>.toEntities() {
        dbFactory.dbQuery {
            forEach {
                require(it.sessionID == null) {
                    "Cannot create entity with non-null sessionID"
                }
                require(it.userId != null) {
                    "Cannot create entity with null userId"
                }

                require(it.refreshToken != null) {
                    "Cannot create entity with null refreshToken"
                }

                require(it.expiresAt != null) {
                    "Cannot create entity with null expiresAt"
                }

                SessionEntity.new {
                    this.userId = EntityID(it.userId, UsersTable)
                    this.token = it.refreshToken
                    this.expiry = it.expiresAt
                }
            }
        }
    }

    fun SessionEntity.toModel() =
        Session(
            sessionID = this.sessionId.value,
            userId = this.userId.value,
            refreshToken = this.token,
            expiresAt = this.expiry,
        )

    fun List<SessionEntity>.toModel() = map { it.toModel() }

    suspend fun SessionEntity.updateModel(model: Session) {
        require(model.sessionID != null) {
            "Cannot update model with null sessionID"
        }

        require(model.sessionID == this.sessionId.value) {
            "Cannot update model with different sessionID"
        }
        require(model.userId != null) {
            "Cannot create entity with null userId"
        }

        require(model.refreshToken != null) {
            "Cannot create entity with null refreshToken"
        }

        require(model.expiresAt != null) {
            "Cannot create entity with null expiresAt"
        }

        dbFactory.dbQuery {
            this.userId = EntityID(model.userId, UsersTable)
            this.token = model.refreshToken
            this.expiry = model.expiresAt
        }
    }
}
