package com.trackmybus.theBouncer.core.errors

import com.trackmybus.theBouncer.core.result.errors.ValidationError
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationErrorTest {
    @Test
    fun invalidEmailHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, ValidationError.InvalidEmail.defaultCode)
    }

    @Test
    fun weakPasswordHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, ValidationError.WeakPassword.defaultCode)
    }

    @Test
    fun emptyFieldHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, ValidationError.EmptyField.defaultCode)
    }

    @Test
    fun invalidCredentialsHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, ValidationError.InvalidCredentials.defaultCode)
    }

    @Test
    fun emailAlreadyExistsHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Conflict, ValidationError.EmailAlreadyExists.defaultCode)
    }

    @Test
    fun unknownErrorHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, ValidationError.UnknownError.defaultCode)
    }
}
