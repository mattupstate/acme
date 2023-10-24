package com.acme.web.api.core.ktor

import com.acme.web.api.security.AcmeWebUserPrincipal
import com.acme.web.api.security.PrincipalNotFoundException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication

fun ApplicationCall.authenticatedUser(): AcmeWebUserPrincipal =
  authentication.principal() ?: throw PrincipalNotFoundException()
