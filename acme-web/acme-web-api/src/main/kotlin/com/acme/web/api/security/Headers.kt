package com.acme.web.api.security

import io.ktor.server.auth.AuthenticationConfig

fun AuthenticationConfig.headers(
  name: String? = null,
  configure: HeaderAuthenticationProvider.Config.() -> Unit
) {
  val provider = HeaderAuthenticationProvider(HeaderAuthenticationProvider.Config(name).apply(configure))
  register(provider)
}
