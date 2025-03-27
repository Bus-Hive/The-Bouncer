package app.bushive.theBouncer.features.v1.data.remote.mapper

import app.bushive.theBouncer.features.v1.data.remote.dto.GoogleTokenDto
import app.bushive.theBouncer.features.v1.data.remote.dto.GoogleUserDto
import app.bushive.theBouncer.features.v1.data.remote.model.GoogleToken
import app.bushive.theBouncer.features.v1.data.remote.model.GoogleUser

object DTOMapper {
    fun GoogleTokenDto.toModel() =
        GoogleToken(
            accessToken = accessToken,
            expiresIn = expiresIn,
            idToken = idToken,
            scope = scope,
            tokenType = tokenType,
        )

    fun GoogleUserDto.toModel() =
        GoogleUser(
            email = email ?: "",
            familyName = familyName ?: "",
            givenName = givenName ?: "",
            id = id ?: "",
            name = name ?: "",
            picture = picture ?: "",
            verifiedEmail = verifiedEmail ?: false,
        )
}
