package com.acme.scheduling.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.exception.DataAccessException
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultTransactionProvider

class JooqAndPostgresListener : TestListener {

  private val logger = KotlinLogging.logger {}

  private var ds: HikariDataSource? = null

  fun testTransaction(block: (config: Configuration) -> Unit) {
    val config = DefaultConfiguration().apply {
      set(ds)
      set(DefaultTransactionProvider(DataSourceConnectionProvider(ds), true))
      set(SQLDialect.POSTGRES)
    }

    try {
      with(config.dsl()) {
        transaction { config ->
          block(config)
          throw TestTransactionException()
        }
      }
    } catch (e: TestTransactionException) {
      logger.debug { e.message }
    }
  }

  override suspend fun beforeSpec(spec: Spec) {
    ds = HikariDataSource(
      HikariConfig().apply {
        jdbcUrl = "jdbc:tc:postgresql:11.5:///test?TC_INITFUNCTION=com.acme.liquibase.LiquibaseTestContainerInitializerKt::update"
        username = "test"
        password = "test"
        isAutoCommit = false
      }
    )
  }

  override suspend fun afterSpec(spec: Spec) {
    ds?.close()
  }

  class TestTransactionException : DataAccessException("Rollback caused by test transaction")
}
