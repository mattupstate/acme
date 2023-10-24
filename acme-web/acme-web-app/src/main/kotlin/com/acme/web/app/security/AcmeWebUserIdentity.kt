package com.acme.web.app.security

import io.ktor.server.auth.Principal
import kotlinx.serialization.Serializable

@Serializable
data class AcmeWebUserIdentity(
  val id: String,
  val state: String,
  val email: String,
  val name: Name
) : Principal {
  @Serializable
  data class Name(
    val given: String,
    val family: String,
  ) {
    val full: String get() = "$given $family".trim()
  }
}
