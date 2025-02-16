package com.trackmybus.theBauncer

import com.trackmybus.theBauncer.config.configureDatabases
import com.trackmybus.theBauncer.config.configureHTTP
import com.trackmybus.theBauncer.config.configureMonitoring
import com.trackmybus.theBauncer.config.configureRouting
import com.trackmybus.theBauncer.config.configureSerialization
import com.trackmybus.theBauncer.config.setupConfig
import com.trackmybus.theBauncer.di.configureKoin
import com.trackmybus.theKeg.config.configureCache
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



