package com.acme.web.server.core.ktor

import io.ktor.server.config.ApplicationConfig

data class OpenTracingConfiguration(
  val serviceName: String,
  val ignorePaths: List<String>
) {
  companion object {
    fun fromConfig(config: ApplicationConfig) =
      OpenTracingConfiguration(
        config.property("serviceName").getString(),
        config.property("ignore").getList(),
      )
  }
}
