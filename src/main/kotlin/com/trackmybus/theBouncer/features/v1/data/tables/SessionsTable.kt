package com.trackmybus.theBouncer.features.v1.data.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object SessionsTable : IdTable<UUID>("sessions") {
    val sessionId = uuid("id").entityId()
    val userId = reference("user_id", UsersTable.userId)
    val token = varchar("token", 765)
    val expiry = datetime("expiry")

    override val id: Column<EntityID<UUID>>
        get() = sessionId
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(sessionId)
}
