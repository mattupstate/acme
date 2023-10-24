package com.acme.web.api.core.ktor

import com.acme.web.api.security.ktor.HeaderAuthConfiguration
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.config.ApplicationConfig

data class AuthenticationConfiguration(
  val headers: HeaderAuthConfiguration
) {

  fun apply(config: AuthenticationConfig) {
    if (headers.enabled) headers.apply(config)
  }

  companion object {
    fun fromConfig(config: ApplicationConfig) =
      AuthenticationConfiguration(
        HeaderAuthConfiguration.fromConfig(config.config("headers"))
      )
  }
}
