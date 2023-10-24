@file:OptIn(ExperimentalSerializationApi::class)

package com.acme.web.app

import com.acme.web.app.config.DataSourceConfiguration
import com.acme.web.app.config.MainConfiguration
import com.acme.web.app.views.appointments
import com.acme.web.app.views.health
import com.acme.web.app.views.root
import com.acme.web.app.views.staticAssets
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultTransactionProvider
import javax.sql.DataSource

val defaultJson = Json {
  prettyPrint = true
  prettyPrintIndent = "  "
}

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
  // val (_, jooqConfig) = dataConfigFactory(config.datasource)

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
