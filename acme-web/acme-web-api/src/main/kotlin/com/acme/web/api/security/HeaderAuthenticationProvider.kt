package com.acme.web.api.security

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationFailedCause
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.Principal
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.response.respond

class HeaderAuthenticationProvider(config: Config) : AuthenticationProvider(config) {
  private val expectedHeaders: Set<String> = config.expectedHeaders!!

  internal val validateFunction: suspend ApplicationCall.(Map<String, String>) -> Principal? = config.validateFunction

  override suspend fun onAuthenticate(context: AuthenticationContext) {
    val call = context.call
    val (credentials, principal) = if (call.request.headers.names().containsAll(expectedHeaders)) {
      expectedHeaders.associateWith {
        call.request.headers[it]!!
      }.let {
        it to call.validateFunction(it)
      }
    } else {
      null to null
    }

    val cause = when {
      credentials == null -> AuthenticationFailedCause.NoCredentials
      principal == null -> AuthenticationFailedCause.InvalidCredentials
      else -> null
    }

    if (cause != null) {
      context.challenge("HeaderAuth", cause) { challenge, _ ->
        call.respond(UnauthorizedResponse())
        challenge.complete()
      }
    }

    if (principal != null) {
      context.principal(principal)
    }
  }

  class Config internal constructor(name: String?) : AuthenticationProvider.Config(name) {
    var expectedHeaders: MutableSet<String>? = null

    internal var validateFunction: suspend ApplicationCall.(Map<String, String>) -> Principal? = {
      throw NotImplementedError()
    }

    fun validate(body: suspend ApplicationCall.(Map<String, String>) -> Principal?) {
      validateFunction = body
    }
  }
}
