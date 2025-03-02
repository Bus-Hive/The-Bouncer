package com.trackmybus.theBouncer.features.v1.data.local.tables

import org.jetbrains.exposed.dao.id.CompositeIdTable

object UsersPermissionGroupTable : CompositeIdTable("user_permission_groups") {
    val userId = reference("user_id", UsersTable.userId)
    val permissionGroupId = reference("permission_group_id", PermissionGroupsTable.id)
    val userPermissionGroupId = integer("entity_id").autoIncrement().entityId()
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(userId, permissionGroupId)
}
