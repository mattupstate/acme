package com.acme.web.api.json

import com.acme.ktor.server.validation.RequestBodyValidationException
import com.acme.web.api.toJsonPointer
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val VndErrorJson = ContentType("application", "vnd.error+json")

suspend fun ApplicationCall.respondVndError(
  statusCode: HttpStatusCode,
  json: Json,
  supplier: () -> VndError
) {
  respondText(json.encodeToString(supplier()), VndErrorJson, statusCode)
}

suspend fun ApplicationCall.respondVndError(message: String, statusCode: HttpStatusCode, json: Json) {
  respondVndError(statusCode, json) { VndError(message = message) }
}

suspend fun ApplicationCall.respondVndError(statusCode: HttpStatusCode, json: Json) {
  respondVndError(statusCode.description, statusCode, json)
}

suspend fun ApplicationCall.respondVndError(statusCode: HttpStatusCode) {
  respondVndError(statusCode.description, statusCode, Json)
}

fun RequestBodyValidationException.toVndError() =
  VndError(
    message = message!!,
    total = if (constraintViolations.isNotEmpty()) constraintViolations.size else null,
    embedded = VndError.Embedded(
      errors = constraintViolations.map { entry ->
        val path = entry.key
        val violations = entry.value

        val preferredViolation = violations.minByOrNull { v ->
          when (v.constraintDescriptor.annotation.annotationClass) {
            NotNull::class -> 0
            NotBlank::class -> 1
            else -> 2
          }
        }!!

        VndError(
          message = preferredViolation.message,
          path = path.toJsonPointer()
        )
      }.sortedBy(VndError::path)
    )
  )
