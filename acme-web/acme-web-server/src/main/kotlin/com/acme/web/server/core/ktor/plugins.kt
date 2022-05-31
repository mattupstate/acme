@file:OptIn(KtorExperimentalLocationsAPI::class)

package com.acme.web.server.core.ktor

import com.acme.core.CommandValidationException
import com.acme.ktor.server.i18n.I18N
import com.acme.ktor.server.logging.StructuredLogging
import com.acme.ktor.server.metrics.PrometheusMetrics
import com.acme.ktor.server.tracing.OpenTracing
import com.acme.ktor.server.tracing.span
import com.acme.ktor.server.validation.RequestBodyValidationException
import com.acme.ktor.server.validation.RequestDecodingException
import com.acme.ktor.server.validation.RequestValidation
import com.acme.web.server.security.PrincipalNotFoundException
import com.acme.web.server.security.UnauthorizedAccessException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.Locations
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.routing.IgnoreTrailingSlash
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.util.UUID

private val appLogger = KotlinLogging.logger {}

fun Application.installOpenTracing(configuration: OpenTracingConfiguration) {
  install(OpenTracing) {
    serviceName = configuration.serviceName
    ignore(configuration.ignorePaths)
  }
}

fun Application.installCallLogging() {
  install(CallLogging) {
    logger = appLogger
    format { call ->
      "${call.request.httpMethod.value} - ${call.request.path()}"
    }
    mdc("principal") {
      it.getPrincipalAsString()
    }
    mdc("trace") {
      try {
        it.span.context().toTraceId()
      } catch (e: Exception) {
        null
      }
    }
  }
}

private fun ApplicationCall.getPrincipalAsString() = try {
  authenticatedUser().id
} catch (e: PrincipalNotFoundException) {
  "anonymous"
}

fun Application.installCallId() {
  install(CallId) {
    retrieveFromHeader(HttpHeaders.XRequestId)
    generate { UUID.randomUUID().toString() }
  }
}

fun Application.installStructuredLogging() {
  install(StructuredLogging) {
    attach {
      mapOf(
        "request_id" to callId!!,
        "principal_id" to getPrincipalAsString()
      )
    }
  }
}

fun Application.installStatusPages(json: Json) {
  install(StatusPages) {
    exception<Throwable> { call, exc ->
      when (exc) {
        is PrincipalNotFoundException -> call.respondVndError(HttpStatusCode.Unauthorized, json)
        is UnauthorizedAccessException -> call.respondVndError(HttpStatusCode.Forbidden, json)
        is NoSuchElementException -> call.respondVndError(HttpStatusCode.NotFound, json)
        is RequestBodyValidationException -> call.onRequestBodyValidationException(json, exc)
        is CommandValidationException -> call.onCommandValidationException(json, exc)
        is RequestDecodingException -> call.onRequestDecodingException(json, exc)
        else -> call.onUnexpectedException(json, exc)
      }
    }
  }
}

fun Application.installContentNegotiation(defaultJson: Json) {
  install(ContentNegotiation) {
    val converter = KotlinxSerializationConverter(defaultJson)
    register(ContentType.Application.HalJson, converter)
    register(ContentType.Application.Json, converter)
  }
}

fun Application.common(configuration: MainConfiguration, json: Json) {
  installCallId()
  install(IgnoreTrailingSlash)
  installStructuredLogging()
  installOpenTracing(configuration.openTracing)
  installCallLogging()
  installStatusPages(json)
  install(Locations)
  install(DefaultHeaders)
  install(XForwardedHeaders)
  installContentNegotiation(json)
  install(I18N)
  install(RequestValidation)
  install(PrometheusMetrics)
}
