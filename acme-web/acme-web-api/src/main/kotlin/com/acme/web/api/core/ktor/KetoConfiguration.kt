package com.acme.web.api.core.ktor

import io.ktor.server.config.ApplicationConfig

data class KetoConfiguration(
  val readUrl: String,
  val writeUrl: String,
) {

  companion object {
    fun fromConfig(config: ApplicationConfig) =
      KetoConfiguration(
        config.property("readUrl").getString(),
        config.property("writeUrl").getString(),
      )
  }
}
