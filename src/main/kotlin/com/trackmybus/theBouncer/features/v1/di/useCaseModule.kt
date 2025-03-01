@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.features.v1.di

import com.trackmybus.theBouncer.di.getLogger
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword.EmailPasswordUseCase
import com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword.EmailPasswordUseCaseImpl
import com.trackmybus.theBouncer.features.v1.domain.usecase.permission.PermissionUseCase
import com.trackmybus.theBouncer.features.v1.domain.usecase.permission.PermissionUseCaseImpl
import com.trackmybus.theBouncer.features.v1.domain.usecase.permissionGroup.PermissionGroupUseCase
import com.trackmybus.theBouncer.features.v1.domain.usecase.permissionGroup.PermissionGroupUseCaseImpl
import com.trackmybus.theBouncer.features.v1.domain.usecase.token.TokenUseCase
import com.trackmybus.theBouncer.features.v1.domain.usecase.token.TokenUseCaseImpl
import org.koin.dsl.module

val useCaseModule =
    module {
        single<EmailPasswordUseCase> {
            EmailPasswordUseCaseImpl(
                getLogger<EmailPasswordUseCase>(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single<TokenUseCase> { TokenUseCaseImpl(getLogger<TokenUseCase>(), get()) }
        single<PermissionUseCase> { PermissionUseCaseImpl(getLogger<UserPermissionGroup>(), get()) }
        single<PermissionGroupUseCase> { PermissionGroupUseCaseImpl(getLogger<PermissionGroupUseCase>(), get(), get()) }
    }
