@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.features.v1.di

import app.bushive.theBouncer.di.getLogger
import app.bushive.theBouncer.features.v1.data.local.dao.permission.PermissionDao
import app.bushive.theBouncer.features.v1.data.local.dao.permission.PermissionDaoImpl
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroup.PermissionGroupDao
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroup.PermissionGroupDaoImpl
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroupPermission.PermissionGroupPermissionDao
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroupPermission.PermissionGroupPermissionDaoImpl
import app.bushive.theBouncer.features.v1.data.local.dao.session.SessionDao
import app.bushive.theBouncer.features.v1.data.local.dao.session.SessionDaoImpl
import app.bushive.theBouncer.features.v1.data.local.dao.user.UserDao
import app.bushive.theBouncer.features.v1.data.local.dao.user.UserDaoImpl
import app.bushive.theBouncer.features.v1.data.local.dao.userPermissionGroup.UserPermissionGroupDao
import app.bushive.theBouncer.features.v1.data.local.dao.userPermissionGroup.UserPermissionGroupDaoImpl
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
