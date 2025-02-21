@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.features.v1.di

import com.trackmybus.theBouncer.di.getLogger
import com.trackmybus.theBouncer.features.v1.domain.repository.jwt.JwtRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.jwt.JwtRepositoryImpl
import com.trackmybus.theBouncer.features.v1.domain.repository.passowrdHash.PasswordHashRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.passowrdHash.PasswordHashRepositoryImpl
import com.trackmybus.theBouncer.features.v1.domain.repository.permission.PermissionRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.permission.PermissionRepositoryImpl
import com.trackmybus.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepositoryImpl
import com.trackmybus.theBouncer.features.v1.domain.repository.session.SessionRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.session.SessionRepositoryImpl
import com.trackmybus.theBouncer.features.v1.domain.repository.user.UserRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.user.UserRepositoryImpl
import com.trackmybus.theBouncer.features.v1.domain.repository.userPermissionGroup.UserPermissionGroupRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.userPermissionGroup.UserPermissionGroupRepositoryImpl
import org.koin.dsl.module

val repositoryModule =
    module {
        single<UserRepository> { UserRepositoryImpl(getLogger<UserRepository>(), get()) }
        single<PermissionRepository> { PermissionRepositoryImpl(getLogger<PermissionRepository>(), get(), get(), get(), get()) }
        single<PermissionGroupRepository> { PermissionGroupRepositoryImpl(getLogger<PermissionGroupRepository>(), get(), get(), get()) }
        single<UserPermissionGroupRepository> { UserPermissionGroupRepositoryImpl(getLogger<UserPermissionGroupRepository>(), get()) }
        single<SessionRepository> { SessionRepositoryImpl(getLogger<SessionRepository>(), get()) }
        single<PasswordHashRepository> { PasswordHashRepositoryImpl(getLogger<PasswordHashRepository>(), get()) }
        single<JwtRepository> { JwtRepositoryImpl(getLogger<JwtRepository>(), get(), get(), get(), get()) }
    }
