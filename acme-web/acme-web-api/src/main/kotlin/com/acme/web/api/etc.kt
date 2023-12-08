@file:OptIn(ExperimentalSerializationApi::class)

package com.acme.web.api

import am.ik.timeflake.Timeflake
import com.acme.web.api.security.AcmeWebUserPrincipal
import com.acme.web.api.security.PrincipalNotFoundException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import jakarta.validation.ElementKind
import jakarta.validation.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

fun ApplicationCall.authenticatedUser(): AcmeWebUserPrincipal =
  authentication.principal() ?: throw PrincipalNotFoundException()

val defaultIdGenerator: () -> String = { Timeflake.generate().base62() }

val defaultJson = Json {
  prettyPrint = true
  prettyPrintIndent = "  "
}

fun Path.toJsonPointer(): String = "/" + map {
  when (it.kind) {
    ElementKind.PROPERTY -> it.name
    ElementKind.CONTAINER_ELEMENT -> it.index
    else -> throw RuntimeException("Unsupported ElementKind: ${it.kind}")
  }
}.joinToString("/")
