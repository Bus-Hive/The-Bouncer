package app.bushive.theBouncer.features.v1.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUserDto(
    @SerialName("email")
    val email: String?,
    @SerialName("family_name")
    val familyName: String?,
    @SerialName("given_name")
    val givenName: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("picture")
    val picture: String?,
    @SerialName("verified_email")
    val verifiedEmail: Boolean?,
)
