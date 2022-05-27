// SEE: https://github.com/jOOQ/jOOQ/issues/9335#issuecomment-770352060
package org.jooq.impl

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.exception.DataAccessException

suspend fun <T> DSLContext.transactionResult(block: suspend (Configuration) -> T): T =
  transactionResult(block, configuration())

private suspend fun <T> transactionResult(block: suspend (Configuration) -> T, config: Configuration): T {
  val ctx = DefaultTransactionContext(config.derive())
  val provider = ctx.configuration().transactionProvider()
  val listeners = TransactionListeners(ctx.configuration())
  var committed = false

  val result: T

  try {
    try {
      listeners.beginStart(ctx)
      provider.begin(ctx)
    } finally {
      listeners.beginEnd(ctx)
    }

    result = block(ctx.configuration())

    try {
      listeners.commitStart(ctx)
      provider.commit(ctx)
      committed = true
    } finally {
      listeners.commitEnd(ctx)
    }
  }
  // [#6608] [#7167] Errors are no longer handled differently
  catch (cause: Throwable) {
    // [#8413] Avoid rollback logic if commit was successful (exception in commitEnd())
    if (!committed) {
      if (cause is Exception)
        ctx.cause(cause)
      else
        ctx.causeThrowable(cause)

      listeners.rollbackStart(ctx)
      try {
        provider.rollback(ctx)
      }
      // [#3718] Use reflection to support also JDBC 4.0
      catch (suppress: Exception) {
        cause.addSuppressed(suppress)
      }
      listeners.rollbackEnd(ctx)
    }
    // [#6608] [#7167] Errors are no longer handled differently
    when (cause) {
      is RuntimeException, is Error -> throw cause
      else -> throw DataAccessException(
        if (committed) "Exception after commit" else "Rollback caused",
        cause
      )
    }
  }

  return result
}
