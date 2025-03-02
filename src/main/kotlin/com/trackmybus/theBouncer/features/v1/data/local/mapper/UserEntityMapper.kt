package com.trackmybus.theBouncer.features.v1.data.local.mapper

import com.trackmybus.theBouncer.features.v1.data.local.entity.UserEntity
import com.trackmybus.theBouncer.features.v1.data.local.model.User

object UserEntityMapper {
    fun UserEntity.toModel() =
        User(
            id = this.userId.value,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            hashedPassword = this.hashedPassword,
            provider = this.provider,
            role = this.role,
            createdAt = this.createdAt,
        )

    fun List<UserEntity>.toModel() = map { it.toModel() }
}
