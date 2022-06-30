package com.acme.web.server.security.ktor

import com.acme.web.server.security.AcmeWebUserPrincipal
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.Principal
import io.ktor.server.config.ApplicationConfig

data class HeaderAuthConfiguration(
  val enabled: Boolean
) {

  fun apply(config: AuthenticationConfig) {
    config.headers {
      expectedHeaders = mutableSetOf(
        "X-Auth-Id",
        "X-Auth-Email",
        "X-Auth-Name-Given",
        "X-Auth-Name-Family",
      )
      validate { credentials ->
        object : AcmeWebUserPrincipal, Principal {
          override val id = credentials["X-Auth-Id"]!!
          override val email = credentials["X-Auth-Email"]!!
          override val name = object : AcmeWebUserPrincipal.Name {
            override val given = credentials["X-Auth-Name-Given"]!!
            override val family = credentials["X-Auth-Name-Family"]!!
            override val preferred = credentials["X-Auth-Name-Preferred"] ?: ""
            override val prefix = credentials["X-Auth-Name-Prefix"] ?: ""
            override val suffix = credentials["X-Auth-Name-Suffix"] ?: ""
          }
        }
      }
    }
  }

  companion object {
    fun fromConfig(config: ApplicationConfig) = HeaderAuthConfiguration(
      config.property("enabled").getString().toBoolean()
    )
  }
}
