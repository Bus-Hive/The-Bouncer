@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.features.v1.di

import app.bushive.theBouncer.features.v1.data.local.ScheduleSchemaInitializer
import org.koin.dsl.module

val featureDatabaseModule =
    module {
        single { ScheduleSchemaInitializer() }
    }
