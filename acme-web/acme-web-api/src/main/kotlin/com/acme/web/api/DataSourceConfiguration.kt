package com.acme.web.api

import io.ktor.server.config.ApplicationConfig

data class DataSourceConfiguration(
  val r2dbcUrl: String,
  val username: String,
  val password: String
) {
  companion object {
    fun fromConfig(config: ApplicationConfig) =
      DataSourceConfiguration(
        config.property("r2dbcUrl").getString(),
        config.property("username").getString(),
        config.property("password").getString()
      )
  }
}
