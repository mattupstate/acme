package com.acme.scheduling.data

import com.acme.liquibase.update
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.r2dbc.spi.ConnectionFactories
import org.jooq.DSLContext
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.jooq.kotlin.coroutines.transactionCoroutine
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer

class TestDatabase {
  private val container = PostgreSQLContainer("postgres:15.5").apply {
    start()
  }

  val dsl = DSL.using(
    ConnectionFactories.get(
      PostgreSQLR2DBCDatabaseContainer.getOptions(container)
    )
  )

  init {
    val ds = HikariDataSource(
      HikariConfig().apply {
        jdbcUrl = container.getJdbcUrl()
        username = container.username
        password = container.password
        isAutoCommit = false
      }
    )
    update(ds.connection)
    ds.close()
  }
}

val database = TestDatabase()

suspend fun testTransaction(block: suspend (dsl: DSLContext) -> Unit) = try {
  database.dsl.transactionCoroutine {
    block(it.dsl())
    throw TestTransactionException()
  }
} catch (_: TestTransactionException) {
  // Do nothing and let Jooq rollback
}

class TestTransactionException : DataAccessException("Rollback caused by test transaction")

