package com.trackmybus.theBouncer.features.v1.data.remote.service.apiClient

import io.ktor.http.HeadersBuilder

fun HeadersBuilder.mapToFormHeaderDataContent(headerMap: Map<String, String>): HeadersBuilder {
    headerMap.forEach { (key, value) ->
        append(key, value)
    }
    return this
}
