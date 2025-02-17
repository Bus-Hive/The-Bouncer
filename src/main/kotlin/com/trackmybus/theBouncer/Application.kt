package com.trackmybus.theBouncer

import com.trackmybus.theBouncer.config.configureDatabases
import com.trackmybus.theBouncer.config.configureHTTP
import com.trackmybus.theBouncer.config.configureMonitoring
import com.trackmybus.theBouncer.config.configureRouting
import com.trackmybus.theBouncer.config.configureSerialization
import com.trackmybus.theBouncer.config.setupConfig
import com.trackmybus.theBouncer.di.configureKoin
import com.trackmybus.theBouncer.config.configureCache
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureHTTP()
    configureMonitoring()
    setupConfig()
    configureDatabases()
    configureSerialization()
    configureCache()
    configureRouting()
}



