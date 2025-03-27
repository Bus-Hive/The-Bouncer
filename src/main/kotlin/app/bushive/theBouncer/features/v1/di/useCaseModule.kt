@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.features.v1.di

import app.bushive.theBouncer.di.getLogger
import app.bushive.theBouncer.features.v1.data.local.model.UserPermissionGroup
import app.bushive.theBouncer.features.v1.domain.usecase.emailPassword.EmailPasswordUseCase
import app.bushive.theBouncer.features.v1.domain.usecase.emailPassword.EmailPasswordUseCaseImpl
import app.bushive.theBouncer.features.v1.domain.usecase.google.GoogleUseCase
import app.bushive.theBouncer.features.v1.domain.usecase.google.GoogleUseCaseImpl
import app.bushive.theBouncer.features.v1.domain.usecase.permission.PermissionUseCase
import app.bushive.theBouncer.features.v1.domain.usecase.permission.PermissionUseCaseImpl
import app.bushive.theBouncer.features.v1.domain.usecase.permissionGroup.PermissionGroupUseCase
import app.bushive.theBouncer.features.v1.domain.usecase.permissionGroup.PermissionGroupUseCaseImpl
import app.bushive.theBouncer.features.v1.domain.usecase.token.TokenUseCase
import app.bushive.theBouncer.features.v1.domain.usecase.token.TokenUseCaseImpl
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
        single<GoogleUseCase> { GoogleUseCaseImpl(getLogger<GoogleUseCase>(), get(), get(), get(), get(), get()) }
    }
