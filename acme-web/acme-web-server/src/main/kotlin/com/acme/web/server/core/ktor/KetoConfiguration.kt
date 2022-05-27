package com.acme.web.server.core.ktor

import io.ktor.server.config.ApplicationConfig

data class KetoConfiguration(
  private val baseUrl: String,
  private val readPort: Int,
  private val writePort: Int,
) {
  val readBaseUrl: String get() = "$baseUrl:$readPort"
  val writeBaseUrl: String get() = "$baseUrl:$writePort"

  companion object {
    fun fromConfig(config: ApplicationConfig) =
      KetoConfiguration(
        config.property("baseUrl").getString(),
        config.property("readPort").getString().toInt(),
        config.property("writePort").getString().toInt(),
      )
  }
}
