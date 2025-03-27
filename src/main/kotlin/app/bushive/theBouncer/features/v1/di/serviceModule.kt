@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.features.v1.di

import app.bushive.theBouncer.di.getLogger
import app.bushive.theBouncer.features.v1.data.remote.service.apiClient.ApiClientService
import app.bushive.theBouncer.features.v1.data.remote.service.google.GoogleService
import app.bushive.theBouncer.features.v1.data.remote.service.google.GoogleServiceImpl
import org.koin.dsl.module

val serviceModules =
    module {
        single { ApiClientService(get(), getLogger<ApiClientService>()) }
        single<GoogleService> { GoogleServiceImpl(getLogger<GoogleService>(), get(), get()) }
    }
