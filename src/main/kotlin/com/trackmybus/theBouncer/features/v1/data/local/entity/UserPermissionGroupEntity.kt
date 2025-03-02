package com.trackmybus.theBouncer.features.v1.data.local.entity

import com.trackmybus.theBouncer.features.v1.data.local.tables.UsersPermissionGroupTable
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.EntityID

class UserPermissionGroupEntity(
    id: EntityID<CompositeID>,
) : CompositeEntity(id) {
    companion object : CompositeEntityClass<UserPermissionGroupEntity>(UsersPermissionGroupTable)

    var userId by UsersPermissionGroupTable.userId
    var permissionGroupId by UsersPermissionGroupTable.permissionGroupId
}
