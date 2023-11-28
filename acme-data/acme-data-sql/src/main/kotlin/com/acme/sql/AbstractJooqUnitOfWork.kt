package com.acme.sql

import com.acme.core.AbstractUnitOfWork
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.kotlin.coroutines.transactionCoroutine

abstract class AbstractJooqUnitOfWork(
  private val config: Configuration
) : AbstractUnitOfWork() {

  private var _dsl: DSLContext? = null

  val dsl get() = _dsl ?: throw RuntimeException("Unit of work missing context")

  override suspend fun <R> transaction(block: suspend () -> R): R =
    config.dsl().transactionCoroutine { config ->
      _dsl = config.dsl()
      block().also {
        _dsl = null
      }
    }
}
