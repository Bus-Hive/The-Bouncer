package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.PermissionGroupEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

object PermissionGroupEntityMapper : KoinComponent {
    private val dbFactory: DatabaseFactory by inject()

    fun PermissionGroupEntity.toModel() =
        com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup(
            id = id.value,
            name = name,
            description = description,
            isBaseUserGroup = isBaseUserGroup,
            createdAt = createdAt,
        )
}
