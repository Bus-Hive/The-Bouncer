package com.trackmybus.theBouncer.core

import com.trackmybus.theBouncer.core.api.ApiResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApiResponseTest {
    @Test
    fun successResponseContainsData() {
        val data = "Test Data"
        val response = ApiResponse.Success(data)
        assertEquals(data, response.data)
    }

    @Test
    fun errorResponseContainsErrorCodeAndMessage() {
        val errorCode = 404
        val message = "Not Found"
        val response = ApiResponse.Error(errorCode, message)
        assertEquals(errorCode, response.errorCode)
        assertEquals(message, response.message)
    }

    @Test
    fun errorResponseCanContainDetails() {
        val errorCode = 500
        val message = "Internal Server Error"
        val details = "Stack trace details"
        val response = ApiResponse.Error(errorCode, message, details)
        assertEquals(details, response.details)
    }

    @Test
    fun errorResponseCanHaveNullDetails() {
        val errorCode = 400
        val message = "Bad Request"
        val response = ApiResponse.Error(errorCode, message)
        assertNull(response.details)
    }
}
