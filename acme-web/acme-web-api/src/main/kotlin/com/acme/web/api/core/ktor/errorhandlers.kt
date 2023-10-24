package com.acme.web.api.core.ktor

import com.acme.core.CommandValidationException
import com.acme.ktor.server.logging.logger
import com.acme.ktor.server.validation.RequestBodyValidationException
import com.acme.ktor.server.validation.RequestDecodingException
import com.acme.ktor.server.validation.getUnderlyingCause
import com.acme.web.api.json.hal.HalLink
import com.acme.web.api.json.hal.VndError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.uri
import kotlinx.serialization.json.Json

suspend fun ApplicationCall.onCommandValidationException(json: Json, exc: CommandValidationException) =
  respondVndError(HttpStatusCode.BadRequest, json) {
    VndError(
      links = VndError.Links(
        help = HalLink("/docs/help.html#validation_error"),
        about = HalLink(request.uri)
      ),
      message = "Validation error",
      total = 1,
      embedded = VndError.Embedded(
        exc.errors.map {
          VndError(
            message = it.message,
            path = when (it) {
              is CommandValidationException.InvalidAggregateReferenceError -> "/${it.fieldName}"
              else -> null
            }
          )
        }.sortedBy(VndError::path)
      )
    )
  }

suspend fun ApplicationCall.onRequestBodyValidationException(json: Json, exc: RequestBodyValidationException) {
  respondVndError(HttpStatusCode.BadRequest, json) {
    exc.toVndError().let {
      it.copy(
        links = it.links.copy(
          help = HalLink("/docs/help.html#validation_error"),
          about = HalLink(request.uri)
        )
      )
    }
  }
}

suspend fun ApplicationCall.onRequestDecodingException(json: Json, exc: RequestDecodingException) {
  respondVndError(HttpStatusCode.BadRequest, json) {
    VndError(
      links = VndError.Links(
        help = HalLink("/docs/help.html#validation_error"),
        about = HalLink(request.uri)
      ),
      message = "Validation error",
      total = 1,
      embedded = VndError.Embedded(
        VndError(
          links = VndError.Links(
            help = HalLink("/docs/help.html#request_decoding_error"),
          ),
          message = exc.getUnderlyingCause()
        )
      )
    )
  }
}

suspend fun ApplicationCall.onUnexpectedException(json: Json, exc: Throwable) {
  logger.error("Unhandled exception", exc)
  respondVndError(HttpStatusCode.InternalServerError, json) {
    VndError(
      links = VndError.Links(
        help = HalLink("/docs/help.html#internal_server_error"),
      ),
      message = "Internal server error"
    )
  }
}
