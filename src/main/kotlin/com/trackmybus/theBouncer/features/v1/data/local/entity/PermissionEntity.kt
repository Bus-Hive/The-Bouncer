package com.trackmybus.theBouncer.features.v1.data.local.entity

import com.trackmybus.theBouncer.features.v1.data.local.tables.PermissionsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PermissionEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<PermissionEntity>(PermissionsTable)

    var name by PermissionsTable.name
    var description by PermissionsTable.description
    var permission by PermissionsTable.permission
    var createdAt by PermissionsTable.createdAt
}
