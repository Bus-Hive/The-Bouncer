@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.di

import app.bushive.theBouncer.config.AppConfig
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.database.postgres.DatabaseFactoryImpl
import org.koin.dsl.module

val configModule =
    module {
        single { AppConfig() }
        single<DatabaseFactory> {
            DatabaseFactoryImpl(
                getLogger<DatabaseFactoryImpl>(),
                get(),
                get(),
            )
        }
        single { get<DatabaseFactory>().database }
    }
