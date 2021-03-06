package com.acme.web.server.core.ktor

import io.ktor.server.config.ApplicationConfig

data class MainConfiguration(
  val datasource: DataSourceConfiguration,
  val authentication: AuthenticationConfiguration,
  val keto: KetoConfiguration,
  val openTracing: OpenTracingConfiguration
) {

  companion object {
    fun fromConfig(config: ApplicationConfig) =
      MainConfiguration(
        DataSourceConfiguration.fromConfig(config.config("datasource")),
        AuthenticationConfiguration.fromConfig(config.config("authentication")),
        KetoConfiguration.fromConfig(config.config("keto")),
        OpenTracingConfiguration.fromConfig(config.config("openTracing"))
      )
  }
}
