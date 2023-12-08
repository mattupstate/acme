package com.acme.web.api.core.ktor

import com.acme.ktor.server.logging.logger
import com.acme.web.api.core.defaultJson
import com.acme.web.api.scheduling.ktor.scheduling
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authentication
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.serialization.json.Json
import org.jooq.impl.DSL

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

  authentication {
    config.authentication.apply(this)
  }

  val (_, jooq) = dataConfigFactory(config.datasource)

  routing {
    route("/scheduling") {
      scheduling(jooq.configuration(), config.keto, "/scheduling")
    }
    route("/health/check") {
      get {
        call.logger.debug("Health check called")
        call.respondText(ContentType.Application.Json) {
          "{\"status\":\"OK\"}"
        }
      }
    }
    staticAssets()
  }
}

fun Application.main() {
  main(MainConfiguration.fromConfig(environment.config))
}
