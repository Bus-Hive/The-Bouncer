package app.bushive.theBouncer

import app.bushive.theBouncer.config.configureCache
import app.bushive.theBouncer.config.configureDatabases
import app.bushive.theBouncer.config.configureHTTP
import app.bushive.theBouncer.config.configureMonitoring
import app.bushive.theBouncer.config.configureRouting
import app.bushive.theBouncer.config.configureSerialization
import app.bushive.theBouncer.config.setupConfig
import app.bushive.theBouncer.di.configureKoin
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
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
