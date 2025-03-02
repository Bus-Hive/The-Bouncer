package com.trackmybus.theBouncer.features.v1.data.remote.mapper

import com.trackmybus.theBouncer.features.v1.data.remote.dto.GoogleTokenDto
import com.trackmybus.theBouncer.features.v1.data.remote.dto.GoogleUserDto
import com.trackmybus.theBouncer.features.v1.data.remote.model.GoogleToken
import com.trackmybus.theBouncer.features.v1.data.remote.model.GoogleUser

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
