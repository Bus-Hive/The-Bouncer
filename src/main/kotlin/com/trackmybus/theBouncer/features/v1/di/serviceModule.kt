@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.features.v1.di

import com.trackmybus.theBouncer.di.getLogger
import com.trackmybus.theBouncer.features.v1.data.remote.service.apiClient.ApiClientService
import com.trackmybus.theBouncer.features.v1.data.remote.service.google.GoogleService
import com.trackmybus.theBouncer.features.v1.data.remote.service.google.GoogleServiceImpl
import org.koin.dsl.module

val serviceModules =
    module {
        single { ApiClientService(get(), getLogger<ApiClientService>()) }
        single<GoogleService> { GoogleServiceImpl(getLogger<GoogleService>(), get(), get()) }
    }
