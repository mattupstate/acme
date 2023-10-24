package com.acme.web.api.core.ktor

import com.acme.ktor.server.logging.logger
import com.acme.web.api.core.defaultJson
import com.acme.web.api.scheduling.ktor.scheduling
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authentication
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultTransactionProvider
import javax.sql.DataSource

fun dataSourceFactory(config: DataSourceConfiguration) = HikariDataSource(
  HikariConfig().apply {
    jdbcUrl = config.jdbcUrl
    username = config.username
    password = config.password
    isAutoCommit = false
  }
)

fun jooqConfigFactory(dataSource: DataSource) = DefaultConfiguration().apply {
  set(dataSource)
  set(DefaultTransactionProvider(DataSourceConnectionProvider(dataSource), true))
  set(SQLDialect.POSTGRES)
}

fun dataConfigFactory(config: DataSourceConfiguration) = dataSourceFactory(config).let {
  it to jooqConfigFactory(it)
}

fun Application.main(config: MainConfiguration, json: Json = defaultJson) {
  common(config, json)

  authentication {
    config.authentication.apply(this)
  }

  val (_, jooqConfig) = dataConfigFactory(config.datasource)

  routing {
    route("/scheduling") {
      scheduling(jooqConfig, config.keto, "/scheduling")
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
