package com.acme.web.app

import com.acme.ktor.server.logging.logger
import io.ktor.server.application.ApplicationCall

suspend fun ApplicationCall.onUnexpectedException(exc: Throwable) {
  logger.error("Unhandled exception", exc)
}
