@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.features.v1.di

import org.koin.core.module.Module

val featureModules =
    listOf<Module>(
        daoModule,
        serviceModules,
        repositoryModule,
        featureDatabaseModule,
        useCaseModule,
    )
