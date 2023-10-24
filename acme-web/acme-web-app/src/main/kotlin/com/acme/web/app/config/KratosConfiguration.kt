package com.acme.web.app.config

import io.ktor.server.config.ApplicationConfig

data class KratosConfiguration(
  val publicUrl: String,
  val selfServiceUiUrl: String,
) {

  val selfServiceLogoutUrl: String get() = "$publicUrl/self-service/logout/browser"
  val selfServiceSettingsUrl: String get() = "$selfServiceUiUrl/settings"

  companion object {
    fun fromConfig(config: ApplicationConfig) =
      KratosConfiguration(
        config.property("publicUrl").getString(),
        config.property("selfServiceUiUrl").getString(),
      )
  }
}
