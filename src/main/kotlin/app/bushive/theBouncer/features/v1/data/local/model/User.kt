package app.bushive.theBouncer.features.v1.data.local.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class User(
    var id: UUID? = null,
    val email: String = "",
    val firstName: String = "",
    var lastName: String = "",
    val hashedPassword: String = "",
    val provider: AuthProvider? = null,
    val createdAt: LocalDateTime? = null,
    val role: Role = Role.USER,
)
