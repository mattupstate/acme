package com.acme.sql

import com.acme.core.AbstractUnitOfWork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.transactionResult as suspendedTransactionalResult

abstract class AbstractJooqUnitOfWork(
  private val config: Configuration,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AbstractUnitOfWork() {

  private var _dsl: DSLContext? = null

  val dsl get() = _dsl ?: throw RuntimeException("Unit of work missing context")

  override suspend fun <R> transaction(block: suspend () -> R): R =
    coroutineScope {
      withContext(dispatcher) {
        config.dsl().suspendedTransactionalResult { config ->
          _dsl = config.dsl()
          block().also {
            _dsl = null
          }
        }
      }
    }
}
