@file:OptIn(ExperimentalUuidApi::class)

package app.bushive.theBouncer.features.v1.data.local.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

data class Session(
    val sessionID: UUID? = null,
    val userId: UUID? = null,
    val refreshToken: String? = null,
    val expiresAt: LocalDateTime? = null,
)
