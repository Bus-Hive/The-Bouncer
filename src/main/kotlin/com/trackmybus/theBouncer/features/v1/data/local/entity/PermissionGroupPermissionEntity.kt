package com.trackmybus.theBouncer.features.v1.data.local.entity

import com.trackmybus.theBouncer.features.v1.data.local.tables.PermissionGroupPermissionTable
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.EntityID

class PermissionGroupPermissionEntity(
    id: EntityID<CompositeID>,
) : CompositeEntity(id) {
    companion object : CompositeEntityClass<PermissionGroupPermissionEntity>(PermissionGroupPermissionTable)

    var permissionGroupId by PermissionGroupPermissionTable.permissionGroupId
    var permissionId by PermissionGroupPermissionTable.permissionId
}
