package com.acme.web.api.core.ktor

import io.ktor.server.config.ApplicationConfig

data class DataSourceConfiguration(
  val jdbcUrl: String,
  val username: String,
  val password: String
) {
  companion object {
    fun fromConfig(config: ApplicationConfig) =
      DataSourceConfiguration(
        config.property("jdbcUrl").getString(),
        config.property("username").getString(),
        config.property("password").getString()
      )
  }
}
