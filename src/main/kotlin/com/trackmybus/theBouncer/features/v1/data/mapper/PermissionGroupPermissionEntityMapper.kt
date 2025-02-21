package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

object PermissionGroupPermissionEntityMapper : KoinComponent {
    private val dbFactory: DatabaseFactory by inject()
}
