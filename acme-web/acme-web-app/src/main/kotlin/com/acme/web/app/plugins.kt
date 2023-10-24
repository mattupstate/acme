package com.acme.web.app

import com.acme.ktor.server.i18n.I18N
import com.acme.ktor.server.logging.StructuredLogging
import com.acme.ktor.server.validation.RequestValidation
import com.acme.web.app.config.MainConfiguration
import com.acme.web.app.config.SessionConfiguration
import com.acme.web.app.security.AcmeWebUserIdentity
import com.acme.web.app.security.AcmeWebUserSession
import com.acme.web.app.security.PrincipalNotFoundException
import com.acme.web.app.security.ktor.headers
import com.acme.web.app.views.authenticatedUser
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.Locations
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.util.hex
import kotlinx.serialization.json.Json
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import org.slf4j.LoggerFactory
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.UUID

private val logger = KotlinLogging.logger {}

private val appLogger = LoggerFactory.getLogger("com.acme.web.app")

fun Application.installContentNegotiation(defaultJson: Json) {
  install(ContentNegotiation) {
    register(ContentType.Application.Json, KotlinxSerializationConverter(defaultJson))
  }
}

fun Application.installCallLogging() {
  install(CallLogging) {
    logger = appLogger
    filter { call ->
      !call.request.path().startsWith("/health")
    }
    format { call ->
      "${call.request.httpMethod.value} - ${call.request.path()}"
    }
    mdc("user_id") {
      it.authenticatedUserId()
    }
  }
}

private fun ApplicationCall.authenticatedUserId() = try {
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
        "user_id" to authenticatedUserId()
      )
    }
  }
}

fun Application.installThymeleaf() {
  install(Thymeleaf) {
    addDialect(LayoutDialect())
    setTemplateResolver(
      ClassLoaderTemplateResolver().apply {
        if (developmentMode) {
          cacheManager = null
        }
        prefix = "templates/"
      }.apply {
        suffix = ".html"
        characterEncoding = "utf-8"
      })
  }
}

fun Application.installHeaderAuthentication() {
  install(Authentication) {
    headers {
      expectedHeaders = mutableSetOf(
        "X-Auth-Id",
        "X-Auth-Email",
        "X-Auth-Name-Given",
        "X-Auth-Name-Family",
      )
      validate { credentials ->
        AcmeWebUserIdentity(
          id = credentials["X-Auth-Id"]!!,
          state = "active",
          email = credentials["X-Auth-Email"]!!,
          name = AcmeWebUserIdentity.Name(
            given = credentials["X-Auth-Name-Given"]!!,
            family = credentials["X-Auth-Name-Family"]!!,
          )
        )
      }
    }
  }
}

fun Application.installSessions(config: SessionConfiguration) {
  install(Sessions) {
    val encryptionKey = hex(config.encryptionKey)
    val signingKey = hex(config.signingKey)
    cookie<AcmeWebUserSession>(config.cookieName) {
      cookie.path = "/"
      cookie.maxAgeInSeconds = 60 * 60 * 24 * 30
      transform(SessionTransportTransformerEncrypt(encryptionKey, signingKey))
    }
  }
}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.common(config: MainConfiguration, json: Json) {
  installCallId()
  installSessions(config.sessions)
  installHeaderAuthentication()
  installThymeleaf()
  installContentNegotiation(json)
  install(IgnoreTrailingSlash)
  installStructuredLogging()
  installCallLogging()
  install(Locations)
  install(DefaultHeaders)
  install(XForwardedHeaders)
  install(I18N)
  install(RequestValidation)
}
