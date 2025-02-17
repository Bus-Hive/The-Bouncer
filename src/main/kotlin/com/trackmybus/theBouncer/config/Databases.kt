package com.trackmybus.theBouncer.config

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Application.configureDatabases() {
    val databaseFactory by inject<DatabaseFactory>()
    databaseFactory.connect()
}