package com.acme.web.server.core.ktor

import com.acme.app.web.server.security.AcmeWebUserPrincipal
import com.acme.app.web.server.security.PrincipalNotFoundException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication

fun ApplicationCall.authenticatedUser(): AcmeWebUserPrincipal =
  authentication.principal() ?: throw PrincipalNotFoundException()
