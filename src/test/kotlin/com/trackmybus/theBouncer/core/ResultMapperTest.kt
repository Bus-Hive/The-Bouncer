package com.trackmybus.theBouncer.core

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultMapperTest {
    @Test
    fun mapResult_successfulTransformation_returnsTransformedResult() {
        val result = Result.success(2)
        val transformedResult = result.mapResult { it * 2 }
        assertTrue(transformedResult.isSuccess)
        Assert.assertEquals(4, transformedResult.getOrNull())
    }

    @Test
    fun mapResult_failedTransformation_returnsFailure() {
        val result = Result.success(2)
        val transformedResult = result.mapResult { throw RuntimeException("Transformation failed") }
        assertTrue(transformedResult.isFailure)
    }

    @Test
    fun mapResult_initialFailure_returnsSameFailure() {
        val result = Result.failure<Int>(RuntimeException("Initial failure"))
        val transformedResult = result.mapResult { it * 2 }
        assertTrue(transformedResult.isFailure)
        assertEquals("Initial failure", transformedResult.exceptionOrNull()?.message)
    }
}
