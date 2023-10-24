@file:OptIn(ExperimentalSerializationApi::class)

package com.acme.web.api.security.keto.ktor

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

fun HttpClientConfig<*>.defaultOryKetoClientConfiguration(baseUrl: String) {
  install(ContentNegotiation) {
    json(
      Json {
        prettyPrint = true
        prettyPrintIndent = "  "
      }
    )
  }
  defaultRequest {
    accept(ContentType.Application.Json)
    contentType(ContentType.Application.Json)
    url(baseUrl)
  }
}

val defaultOryKetoReadClient = HttpClient {
  defaultOryKetoClientConfiguration("http://localhost:4466")
}

val defaultOryKetoWriteClient = HttpClient {
  defaultOryKetoClientConfiguration("http://localhost:4467")
}
