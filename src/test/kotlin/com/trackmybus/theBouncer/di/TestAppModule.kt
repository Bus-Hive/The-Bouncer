package com.trackmybus.theBouncer.di

import com.trackmybus.theBouncer.features.v1.di.featureModules
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.context.startKoin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun configureKoinUnitTest() {
    startKoin {
        slf4jLogger()
        modules(
            testConfigModulesForUnitTest,
            loggerModule,
            *featureModules.toTypedArray(),
        )
    }
}

fun Application.configureKoinServerTest() {
    install(Koin) {
        slf4jLogger()
        modules(
            testConfigModulesForIntegrationTest,
            loggerModule,
            *featureModules.toTypedArray(),
        )
    }
}
