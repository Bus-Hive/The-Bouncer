package app.bushive.theBouncer.di

import app.bushive.theBouncer.features.v1.di.featureModules
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            configModule,
            loggerModule,
            networkModule,
            *featureModules.toTypedArray(),
        )
    }
}
