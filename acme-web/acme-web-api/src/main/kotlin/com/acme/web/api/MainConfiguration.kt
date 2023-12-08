package com.acme.web.api

import io.ktor.server.config.ApplicationConfig

data class MainConfiguration(
  val datasource: DataSourceConfiguration,
  val authentication: AuthenticationConfiguration,
  val keto: KetoConfiguration,
) {

  companion object {
    fun fromConfig(config: ApplicationConfig) =
      MainConfiguration(
        DataSourceConfiguration.fromConfig(config.config("datasource")),
        AuthenticationConfiguration.fromConfig(config.config("authentication")),
        KetoConfiguration.fromConfig(config.config("keto")),
      )
  }
}
