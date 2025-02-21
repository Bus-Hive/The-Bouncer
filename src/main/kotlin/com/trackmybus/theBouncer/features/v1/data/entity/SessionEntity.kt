package com.trackmybus.theBouncer.features.v1.data.entity

import com.trackmybus.theBouncer.features.v1.data.tables.SessionsTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class SessionEntity(
    id: EntityID<UUID>,
): Entity<UUID>(id) {
    companion object : EntityClass<UUID, SessionEntity>(SessionsTable)

    var sessionId by SessionsTable.sessionId
    var userId by SessionsTable.userId
    var token by SessionsTable.token
    var expiry by SessionsTable.expiry
}