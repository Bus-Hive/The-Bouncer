package app.bushive.theBouncer.features.v1.data.local.entity

import app.bushive.theBouncer.features.v1.data.local.tables.PermissionGroupsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PermissionGroupEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<PermissionGroupEntity>(PermissionGroupsTable)

    var name by PermissionGroupsTable.name
    var description by PermissionGroupsTable.description
    var isBaseUserGroup by PermissionGroupsTable.isBaseUserGroup
    var createdAt by PermissionGroupsTable.createdAt
}
