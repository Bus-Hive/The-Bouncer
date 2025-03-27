package app.bushive.theBouncer.features.v1.data.local.entity

import app.bushive.theBouncer.features.v1.data.local.tables.UsersTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class UserEntity(
    id: EntityID<UUID>,
) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, UserEntity>(UsersTable)

    var userId by UsersTable.userId
    var email by UsersTable.email
    var firstName by UsersTable.firstName
    var lastName by UsersTable.lastName
    var hashedPassword by UsersTable.hashedPassword
    var provider by UsersTable.provider
    var role by UsersTable.role
    var createdAt by UsersTable.createdAt
}
