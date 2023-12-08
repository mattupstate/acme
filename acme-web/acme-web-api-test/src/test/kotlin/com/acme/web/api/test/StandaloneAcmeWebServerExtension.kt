package com.acme.web.api.test

import com.acme.liquibase.update
import com.acme.web.api.core.ktor.AuthenticationConfiguration
import com.acme.web.api.core.ktor.DataSourceConfiguration
import com.acme.web.api.core.ktor.KetoConfiguration
import com.acme.web.api.core.ktor.MainConfiguration
import com.acme.web.api.core.ktor.main
import com.acme.web.api.security.ktor.HeaderAuthConfiguration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.listeners.ProjectListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.NettyApplicationEngine
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import java.io.IOException
import java.net.ServerSocket

/**
 * Runs the following services to support testing the acme-web-api module HTTP interface:
 * - ORY Keto using Docker
 * - acme-web-api using Ktor's embedded server feature
 */
class StandaloneAcmeWebServerExtension(
  private val serverHost: String,
  private val serverPort: Int,
) : ProjectListener {

  private var server: NettyApplicationEngine? = null

  private val keto: GenericContainer<Nothing> =
    GenericContainer<Nothing>("oryd/keto:v0.8.0").apply {
      startupAttempts = 1
      commandParts = arrayOf("serve", "-c", "/etc/keto.yml")
      withClasspathResourceMapping("etc/keto.yml", "/etc/keto.yml", BindMode.READ_ONLY)
      withExposedPorts(4466, 4467)
    }

  private val postgres = PostgreSQLContainer("postgres:15.5")

  val http = HttpClient {
    expectSuccess = false

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
      host = serverHost
      port = serverPort
      accept(ContentType.Application.Json)
    }
  }

  private fun runMigrations() {
    val ds = HikariDataSource(
      HikariConfig().apply {
        jdbcUrl = postgres.getJdbcUrl()
        username = postgres.username
        password = postgres.password
        isAutoCommit = false
      }
    )
    update(ds.connection)
    ds.close()
  }

  override suspend fun beforeProject() {
    keto.start()
    postgres.start()
    runMigrations()

    server = embeddedServer(
      io.ktor.server.netty.Netty,
      environment = applicationEngineEnvironment {
        module {
          main(
            MainConfiguration(
              datasource = DataSourceConfiguration(
                r2dbcUrl = postgres.getJdbcUrl().replace("jdbc:", "r2dbc:"),
                username = postgres.username,
                password = postgres.password,
              ),
              authentication = AuthenticationConfiguration(
                headers = HeaderAuthConfiguration(
                  enabled = true
                )
              ),
              keto = KetoConfiguration(
                readUrl = "http://127.0.0.1:${keto.getMappedPort(4466)}",
                writeUrl = "http://127.0.0.1:${keto.getMappedPort(4467)}",
              ),
            )
          )
        }
        developmentMode = true
        connector {
          port = serverPort
          host = serverHost
        }
      }
    ).also {
      it.start()
    }
  }

  override suspend fun afterProject() {
    server?.stop(3000, 5000)
    keto.stop()
    postgres.stop()
  }

  companion object {
    private val FREE_PORT_RANGE = 32768..61000

    fun findOpenPort(): Int {
      for (port in FREE_PORT_RANGE) {
        try {
          ServerSocket(port).use { serverSocket ->
            serverSocket shouldNotBe null
            serverSocket.localPort shouldBe port
            return port
          }
        } catch (e: IOException) {
          e shouldHaveMessage "Address already in use"
        }
      }
      error("No free port in the range found")
    }
  }
}
