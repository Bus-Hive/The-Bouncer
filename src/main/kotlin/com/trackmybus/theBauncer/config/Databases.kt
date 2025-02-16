package com.trackmybus.theBauncer.config

import com.trackmybus.theBauncer.database.postgres.DatabaseFactory
import io.ktor.server.application.Application
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Application.configureDatabases() {
    val databaseFactory by inject<DatabaseFactory>()
    databaseFactory.connect()
}