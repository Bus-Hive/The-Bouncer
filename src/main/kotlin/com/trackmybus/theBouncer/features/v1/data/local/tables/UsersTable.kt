package com.trackmybus.theBouncer.features.v1.data.local.tables

import com.trackmybus.theBouncer.features.v1.data.local.model.AuthProvider
import com.trackmybus.theBouncer.features.v1.data.local.model.Role
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import java.util.UUID

object UsersTable : IdTable<UUID>("users") {
    val userId = uuid("id").autoGenerate().entityId()
    val email = varchar("email", 255)
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val hashedPassword = varchar("hashed_password", 255)
    val provider = enumeration<AuthProvider>("provider")
    val role = enumeration<Role>("role")
    val createdAt = datetime("created_at")

    override val id: Column<EntityID<UUID>>
        get() = userId
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(userId)
}
