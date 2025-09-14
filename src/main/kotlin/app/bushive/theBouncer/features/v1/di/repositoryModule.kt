@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.features.v1.di

import app.bushive.theBouncer.di.getLogger
import app.bushive.theBouncer.features.v1.domain.repository.google.GoogleRepository
import app.bushive.theBouncer.features.v1.domain.repository.google.GoogleRepositoryImpl
import app.bushive.theBouncer.features.v1.domain.repository.jwt.JwtRepository
import app.bushive.theBouncer.features.v1.domain.repository.jwt.JwtRepositoryImpl
import app.bushive.theBouncer.features.v1.domain.repository.passowrdHash.PasswordHashRepository
import app.bushive.theBouncer.features.v1.domain.repository.passowrdHash.PasswordHashRepositoryImpl
import app.bushive.theBouncer.features.v1.domain.repository.permission.PermissionRepository
import app.bushive.theBouncer.features.v1.domain.repository.permission.PermissionRepositoryImpl
import app.bushive.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepository
import app.bushive.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepositoryImpl
import app.bushive.theBouncer.features.v1.domain.repository.session.SessionRepository
import app.bushive.theBouncer.features.v1.domain.repository.session.SessionRepositoryImpl
import app.bushive.theBouncer.features.v1.domain.repository.user.UserRepository
import app.bushive.theBouncer.features.v1.domain.repository.user.UserRepositoryImpl
import app.bushive.theBouncer.features.v1.domain.repository.userPermissionGroup.UserPermissionGroupRepository
import app.bushive.theBouncer.features.v1.domain.repository.userPermissionGroup.UserPermissionGroupRepositoryImpl
import org.koin.dsl.module

val repositoryModule =
    module {
        single<UserRepository> { UserRepositoryImpl(getLogger<UserRepository>(), get()) }
        single<PermissionRepository> {
            PermissionRepositoryImpl(
                getLogger<PermissionRepository>(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single<PermissionGroupRepository> {
            PermissionGroupRepositoryImpl(
                getLogger<PermissionGroupRepository>(),
                get(),
                get(),
                get(),
            )
        }
        single<UserPermissionGroupRepository> {
            UserPermissionGroupRepositoryImpl(
                getLogger<UserPermissionGroupRepository>(),
                get(),
            )
        }
        single<SessionRepository> { SessionRepositoryImpl(getLogger<SessionRepository>(), get()) }
        single<PasswordHashRepository> { PasswordHashRepositoryImpl(getLogger<PasswordHashRepository>(), get()) }
        single<JwtRepository> { JwtRepositoryImpl(getLogger<JwtRepository>(), get(), get(), get(), get()) }
        single<GoogleRepository> { GoogleRepositoryImpl(get(), get()) }
    }
