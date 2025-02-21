@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.features.v1.di

import com.trackmybus.theBouncer.di.getLogger
import com.trackmybus.theBouncer.features.v1.data.dao.permission.PermissionDao
import com.trackmybus.theBouncer.features.v1.data.dao.permission.PermissionDaoImpl
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup.PermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup.PermissionGroupDaoImpl
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroupPermission.PermissionGroupPermissionDao
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroupPermission.PermissionGroupPermissionDaoImpl
import com.trackmybus.theBouncer.features.v1.data.dao.session.SessionDao
import com.trackmybus.theBouncer.features.v1.data.dao.session.SessionDaoImpl
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDaoImpl
import com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup.UserPermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup.UserPermissionGroupDaoImpl
import org.koin.dsl.module

val daoModule =
    module {
        single<UserDao> { UserDaoImpl(getLogger<UserDao>(), get()) }
        single<SessionDao> { SessionDaoImpl(getLogger<SessionDao>(), get()) }
        single<PermissionDao> { PermissionDaoImpl(getLogger<PermissionDao>(), get()) }
        single<UserPermissionGroupDao> { UserPermissionGroupDaoImpl(getLogger<UserPermissionGroupDao>(), get()) }
        single<PermissionGroupDao> { PermissionGroupDaoImpl(getLogger<UserPermissionGroupDao>(), get()) }
        single<PermissionGroupPermissionDao> { PermissionGroupPermissionDaoImpl(getLogger<UserPermissionGroupDao>(), get()) }
    }
