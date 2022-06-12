package com.acme.web.test.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.Instant
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.util.UUID

class KratosUserFixtureService(host: String, port: Int) : UserFixtureService {

  private val http = HttpClient {
    install(HttpTimeout) {
      requestTimeoutMillis = 10000
      connectTimeoutMillis = 10000
      socketTimeoutMillis = 10000
    }

    install(Logging) {
      logger = Logger.DEFAULT
      level = LogLevel.ALL
    }

    defaultRequest {
      this.host = host
      this.port = port
      accept(ContentType.Application.Json)
    }
  }

  private fun isoDateTimeNow() = ISO_DATE_TIME.format(Instant.now())

  override suspend fun createVerifiedUser(email: String, password: String, id: String) {
    val now = isoDateTimeNow()
    val result = http.post("/admin/identities") {
      contentType(ContentType.Application.Json)
      setBody(
        """
          {
            "credentials": {
              "password": {
                "config": {
                  "password": "$password"
                }
              }
            },
            "metadata_admin": null,
            "metadata_public": null,
            "recovery_addresses": [
              {
                "created_at": "$now",
                "id": "${UUID.randomUUID()}",
                "updated_at": "$now",
                "value": "$email",
                "via": "email"
              }
            ],
            "schema_id": "default",
            "state": "active",
            "traits": {
              "email": "$email",
              "name": {
                "given": "Test,
                "family": "User",
                "preferred": "Test User"
              }
            },
            "verifiable_addresses": [
              {
                "created_at": "$now",
                "id": "${UUID.randomUUID()}",
                "status": "active",
                "updated_at": "$now",
                "value": "$email",
                "verified": true,
                "verified_at": "$now",
                "via": "email"
              }
            ]
          }
          """
      )
    }
  }

  override suspend fun deleteUser(user: String) {
    TODO("Not yet implemented")
  }
}
