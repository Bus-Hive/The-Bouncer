package com.trackmybus.theBouncer.core

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.getError
import com.trackmybus.theBouncer.core.result.ResultHandler.isFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.isSuccess
import com.trackmybus.theBouncer.core.result.ResultHandler.toErrorResult
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.DataError
import io.ktor.http.HttpStatusCode
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertNull

class ResultMapperTest {
    @Test
    fun mapResult_successfulTransformation_returnsTransformedResult() {
        val result = Result.Success<Int, RootError>(2)
        val transformedResult = result.mapResult { it * 2 }
        assertTrue(transformedResult.isSuccess())
        Assert.assertEquals(4, transformedResult.getDataOrNull())
    }

    @Test
    fun mapResult_failedTransformation_returnsFailure() {
        val result = Result.Success<Int, RootError>(2)
        val transformedResult = result.mapResult { throw RuntimeException("Transformation failed") }
        assertTrue(transformedResult.isFailure())
    }

    @Test
    fun mapResult_initialFailure_returnsSameFailure() {
        val result = Result.Error<Int, RootError>(DataError.Local.RecordNotFound)
        val transformedResult = result.mapResult { it * 2 }
        assertTrue(transformedResult.isFailure())
    }

    @Test
    fun getErrorThrowsExceptionForSuccess() {
        val successResult = Result.Success<String, RootError>(data = "data", code = HttpStatusCode.OK)
        assertThrows(IllegalStateException::class.java) { successResult.getError() }
    }

    @Test
    fun getErrorReturnsErrorForError() {
        val errorResult = Result.Error(error = DataError.Network.SendFailed, code = HttpStatusCode.BadGateway, data = "error")
        assertEquals(DataError.Network.SendFailed, errorResult.getError())
    }

    @Test
    fun toErrorResultThrowsExceptionForSuccess() {
        val successResult = Result.Success<String, RootError>(data = "data", code = HttpStatusCode.OK)
        assertThrows(IllegalStateException::class.java) { successResult.toErrorResult<String, RootError>() }
    }

    @Test
    fun toErrorResultConvertsError() {
        val errorResult = Result.Error(error = DataError.Network.SendFailed, code = HttpStatusCode.BadGateway, data = "error")
        val convertedResult = errorResult.toErrorResult<String, DataError.Network>()
        assertTrue(convertedResult is Result.Error)
        assertEquals(DataError.Network.SendFailed, (convertedResult as Result.Error).error)
        assertNull(convertedResult.data)
        assertEquals(HttpStatusCode.BadGateway, convertedResult.code)
    }
}
