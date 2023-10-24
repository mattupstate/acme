package com.acme.web.app.config

import io.ktor.server.config.ApplicationConfig

data class SessionConfiguration(
  val cookieName: String,
  val encryptionKey: String,
  val signingKey: String,
) {
  companion object {
    fun fromConfig(config: ApplicationConfig) =
      SessionConfiguration(
        config.property("cookieName").getString(),
        config.property("encryptionKey").getString(),
        config.property("signingKey").getString(),
      )
  }
}
