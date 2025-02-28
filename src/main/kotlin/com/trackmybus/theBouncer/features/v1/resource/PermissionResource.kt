package com.trackmybus.theBouncer.features.v1.resource

import com.trackmybus.theBouncer.config.TheBouncer
import com.trackmybus.theBouncer.core.api.respondError
import com.trackmybus.theBouncer.core.result.ResultHandler.respondResult
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionRequestDto
import com.trackmybus.theBouncer.features.v1.domain.usecase.permission.PermissionUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

@Resource("/permissions")
class Permissions(
    val parent: TheBouncer.V1.Protected,
    val permissionId: Int? = null,
) {
    @Resource("/all")
    class All(
        val parent: Permissions,
    )

    @Resource("/user")
    class User(
        val parent: Permissions,
        val userId: String? = null,
    )
}

fun Application.permissionRoutes() {
    routing {
        addPermission()
        removePermission()
        updatePermission()
        getPermission()
        getPermissions()
        getUserPermissions()
    }
}

fun Route.addPermission() {
    val permissionUseCase: PermissionUseCase by application.inject()
    post<Permissions, PermissionRequestDto> { resource, body ->
        runCatching {
            permissionUseCase.addPermission(
                name = body.name,
                description = body.description,
                permission = body.permission,
            )
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to add permission",
                details = it.message,
            )
        }
    }
}

fun Route.removePermission() {
    val permissionUseCase: PermissionUseCase by application.inject()
    delete<Permissions> { resource ->
        runCatching {
            require(resource.permissionId != null) {
                "Permission ID is required"
            }
            permissionUseCase.removePermission(resource.permissionId)
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to remove permission",
                details = it.message,
            )
        }
    }
}

fun Route.updatePermission() {
    val permissionUseCase: PermissionUseCase by application.inject()
    put<Permissions, PermissionRequestDto> { resource, body ->
        runCatching {
            require(resource.permissionId != null) {
                "Permission ID is required"
            }
            permissionUseCase.updatePermission(
                permissionId = resource.permissionId,
                name = body.name,
                description = body.description,
                permission = body.permission,
            )
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to update permission",
                details = it.message,
            )
        }
    }
}

fun Route.getPermission() {
    val permissionUseCase: PermissionUseCase by application.inject()
    get<Permissions> { resource ->
        runCatching {
            require(resource.permissionId != null) {
                "Permission ID is required"
            }
            permissionUseCase.getPermission(resource.permissionId)
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to fetch permission",
                details = it.message,
            )
        }
    }
}

fun Route.getPermissions() {
    val permissionUseCase: PermissionUseCase by application.inject()
    get<Permissions.All> { resource ->
        runCatching {
            permissionUseCase.getPermissions()
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to fetch permissions",
                details = it.message,
            )
        }
    }
}

fun Route.getUserPermissions() {
    val permissionUseCase: PermissionUseCase by application.inject()
    get<Permissions.User> { resource ->
        runCatching {
            require(resource.userId != null) {
                "User ID is required"
            }
            permissionUseCase.getPermissionsForUser(resource.userId)
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to fetch user permissions",
                details = it.message,
            )
        }
    }
}
