package com.trackmybus.theBouncer.features.v1.data.local.tables

import org.jetbrains.exposed.dao.id.CompositeIdTable

object PermissionGroupPermissionTable : CompositeIdTable("permission_group_permission") {
    val permissionGroupId = reference("permission_group_id", PermissionGroupsTable.id)
    val permissionId = reference("permission_id", PermissionsTable.id)
    val permissionGroupPermissionId = integer("entity_id").autoIncrement().entityId()
    override val primaryKey = PrimaryKey(permissionGroupId, permissionId)
}
