package com.acme.web.app.config

import io.ktor.server.config.ApplicationConfig

data class MainConfiguration(
  val datasource: DataSourceConfiguration,
  val kratosConfiguration: KratosConfiguration,
  val keto: KetoConfiguration,
  val sessions: SessionConfiguration,
) {

  companion object {
    fun fromConfig(config: ApplicationConfig) =
      MainConfiguration(
        DataSourceConfiguration.fromConfig(config.config("datasource")),
        KratosConfiguration.fromConfig(config.config("kratos")),
        KetoConfiguration.fromConfig(config.config("keto")),
        SessionConfiguration.fromConfig(config.config("sessions"))
      )
  }
}
