package com.acme.web.api.scheduling

import com.acme.web.api.core.toJsonPointer
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.Tag
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.exception.DataAccessException
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultTransactionProvider

internal val logger = KotlinLogging.logger {}

const val DB_URL =
  "jdbc:tc:postgresql:11.5:///test?TC_INITFUNCTION=com.acme.liquibase.LiquibaseTestContainerInitializerKt::update"

val ds by lazy {
  HikariDataSource(
    HikariConfig().apply {
      jdbcUrl = DB_URL
      username = "test"
      password = "test"
      isAutoCommit = false
    }
  )
}

val jooq by lazy {
  DefaultConfiguration().apply {
    set(ds)
    set(DefaultTransactionProvider(DataSourceConnectionProvider(ds), true))
    set(SQLDialect.POSTGRES)
  }
}

class TestTransactionException : DataAccessException("Rollback caused by test transaction")

fun testTransaction(block: (config: Configuration) -> Unit) {
  try {
    with(jooq.dsl()) {
      transaction { config ->
        block(config)
        throw TestTransactionException()
      }
    }
  } catch (e: TestTransactionException) {
    logger.debug { e.message }
  }
}

object SchedulingTest : Tag()

val constraintValidator: Validator = Validation.buildDefaultValidatorFactory().validator

fun withJsonPointerViolations(obj: Any, block: (Map<String, Set<String>>) -> Unit) {
  block(
    constraintValidator.validate(obj).groupBy {
      it.propertyPath.toJsonPointer()
    }.map {
      it.key to it.value.map(ConstraintViolation<*>::getMessage).toSet()
    }.toMap()
  )
}
