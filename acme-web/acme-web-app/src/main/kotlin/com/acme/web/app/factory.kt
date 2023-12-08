@file:OptIn(ExperimentalSerializationApi::class)

package com.acme.web.app

import com.acme.web.app.config.DataSourceConfiguration
import com.acme.web.app.config.MainConfiguration
import com.acme.web.app.views.appointments
import com.acme.web.app.views.health
import com.acme.web.app.views.root
import com.acme.web.app.views.staticAssets
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.jooq.impl.DSL

val defaultJson = Json {
  prettyPrint = true
  prettyPrintIndent = "  "
}

fun connectionFactory(config: DataSourceConfiguration): ConnectionFactory = ConnectionFactories.get(
  ConnectionFactoryOptions.parse(config.r2dbcUrl).mutate()
    .option(ConnectionFactoryOptions.USER, config.username)
    .option(ConnectionFactoryOptions.PASSWORD, config.password)
    .build()
)

fun jooqConfigFactory(connectionFactory: ConnectionFactory) = DSL.using(connectionFactory)

fun dataConfigFactory(config: DataSourceConfiguration) = connectionFactory(config).let {
  it to jooqConfigFactory(it)
}

fun Application.main(config: MainConfiguration, json: Json = defaultJson) {
  common(config, json)
  val (_, jooq) = dataConfigFactory(config.datasource)

  routing {
    authenticate {
      root()
      appointments()
    }
    health()
    staticAssets()
  }
}

fun Application.main() {
  main(MainConfiguration.fromConfig(environment.config))
}
