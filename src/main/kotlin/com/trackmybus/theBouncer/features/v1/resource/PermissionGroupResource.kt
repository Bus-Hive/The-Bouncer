package com.trackmybus.theBouncer.features.v1.resource

import com.trackmybus.theBouncer.config.TheBouncer
import com.trackmybus.theBouncer.core.api.respondError
import com.trackmybus.theBouncer.core.result.ResultHandler.respondResult
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionGroupRequestDto
import com.trackmybus.theBouncer.features.v1.domain.usecase.permissionGroup.PermissionGroupUseCase
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

@Resource("/permission-group")
class PermissionGroup(
    val parent: TheBouncer.V1.Protected,
    val groupId: Int? = null,
) {
    @Resource("/all")
    class All(
        val parent: PermissionGroup,
    )

    @Resource("/base")
    class Base(
        val parent: PermissionGroup,
    )

    @Resource("/user")
    class User(
        val parent: PermissionGroup,
        val userId: String? = null,
    )

    @Resource("/permission")
    class Permission(
        val parent: PermissionGroup,
        val permissionId: Int? = null,
    )
}

fun Application.permissionGroupRoutes() {
    routing {
        addPermissionGroup()
        removePermissionGroup()
        updatePermissionGroup()
        assignPermissionToGroup()
        removePermissionFromGroup()
        getPermissionGroup()
        getPermissionGroups()
        getBasePermissionGroups()
        getPermissionGroupsByUserId()
        addUserToPermissionGroup()
        removeUserFromPermissionGroup()
    }
}

fun Route.addPermissionGroup() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    post<PermissionGroup, PermissionGroupRequestDto> { resource, body ->
        runCatching {
            permissionGroupUseCase.addPermissionGroup(
                name = body.name,
                description = body.description,
                isBaseUserGroup = body.isBaseUserGroup,
            )
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to add permission group",
                details = it.message,
            )
        }
    }
}

fun Route.removePermissionGroup() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    delete<PermissionGroup> { resource ->
        require(resource.groupId != null) {
            "Group ID is required"
        }

        runCatching {
            permissionGroupUseCase.removePermissionGroup(resource.groupId)
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to remove permission group",
                details = it.message,
            )
        }
    }
}

fun Route.updatePermissionGroup() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    put<PermissionGroup, PermissionGroupRequestDto> { resource, body ->
        require(resource.groupId != null) {
            "Group ID is required"
        }

        runCatching {
            permissionGroupUseCase.updatePermissionGroup(
                permissionGroupId = resource.groupId,
                name = body.name,
                description = body.description,
                isBaseUserGroup = body.isBaseUserGroup,
            )
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to update permission group",
                details = it.message,
            )
        }
    }
}

fun Route.assignPermissionToGroup() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    post<PermissionGroup.Permission> { resource ->
        require(resource.parent.groupId != null) {
            "Group ID is required"
        }

        require(resource.permissionId != null) {
            "Permission ID is required"
        }

        runCatching {
            permissionGroupUseCase.assignPermissionToGroup(
                permissionGroupId = resource.parent.groupId,
                permissionId = resource.permissionId,
            )
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to assign permission to group",
                details = it.message,
            )
        }
    }
}

fun Route.removePermissionFromGroup() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    delete<PermissionGroup.Permission> { resource ->
        require(resource.parent.groupId != null) {
            "Group ID is required"
        }

        require(resource.permissionId != null) {
            "Permission ID is required"
        }

        runCatching {
            permissionGroupUseCase.removePermissionFromGroup(
                permissionGroupId = resource.parent.groupId,
                permissionId = resource.permissionId,
            )
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to remove permission from group",
                details = it.message,
            )
        }
    }
}

fun Route.getPermissionGroup() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    get<PermissionGroup> { resource ->
        require(resource.groupId != null) {
            "Group ID is required"
        }

        runCatching {
            permissionGroupUseCase.getPermissionGroup(resource.groupId)
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to fetch permission group",
                details = it.message,
            )
        }
    }
}

fun Route.getPermissionGroups() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    get<PermissionGroup.All> { resource ->
        runCatching {
            permissionGroupUseCase.getPermissionGroups()
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to fetch permission groups",
                details = it.message,
            )
        }
    }
}

fun Route.getBasePermissionGroups() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    get<PermissionGroup.Base> { resource ->
        runCatching {
            permissionGroupUseCase.getBasePermissionGroups()
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to fetch base permission groups",
                details = it.message,
            )
        }
    }
}

fun Route.getPermissionGroupsByUserId() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    get<PermissionGroup.User> { resource ->
        require(resource.userId != null) {
            "User ID is required"
        }

        runCatching {
            permissionGroupUseCase.getPermissionGroupsByUserId(resource.userId)
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to fetch permission groups by user ID",
                details = it.message,
            )
        }
    }
}

fun Route.addUserToPermissionGroup() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    post<PermissionGroup.User> { resource ->
        require(resource.userId != null) {
            "User ID is required"
        }

        require(resource.parent.groupId != null) {
            "Group ID is required"
        }

        runCatching {
            permissionGroupUseCase.assignUserToGroup(resource.userId, resource.parent.groupId)
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to add user to group",
                details = it.message,
            )
        }
    }
}

fun Route.removeUserFromPermissionGroup() {
    val permissionGroupUseCase: PermissionGroupUseCase by application.inject()
    delete<PermissionGroup.User> { resource ->
        require(resource.userId != null) {
            "User ID is required"
        }

        require(resource.parent.groupId != null) {
            "Group ID is required"
        }

        runCatching {
            permissionGroupUseCase.removeUserFromGroup(resource.userId, resource.parent.groupId)
        }.onSuccess {
            call.respondResult(it)
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to remove user from group",
                details = it.message,
            )
        }
    }
}
