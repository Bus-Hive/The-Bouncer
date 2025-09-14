package app.bushive.theBouncer.features.v1.data.remote.model

data class GoogleUser(
    val email: String,
    val familyName: String,
    val givenName: String,
    val id: String,
    val name: String,
    val picture: String,
    val verifiedEmail: Boolean,
)
