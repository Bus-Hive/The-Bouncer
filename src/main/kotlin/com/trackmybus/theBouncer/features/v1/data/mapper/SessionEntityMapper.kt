package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.features.v1.data.entity.SessionEntity
import com.trackmybus.theBouncer.features.v1.data.model.Session

object SessionEntityMapper {
    fun SessionEntity.toModel() =
        Session(
            sessionID = this.sessionId.value,
            userId = this.userId.value,
            refreshToken = this.token,
            expiresAt = this.expiry,
        )

    fun List<SessionEntity>.toModel() = map { it.toModel() }
}
