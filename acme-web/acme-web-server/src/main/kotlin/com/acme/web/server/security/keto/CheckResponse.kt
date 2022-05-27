package com.acme.web.server.security.keto

import kotlinx.serialization.Serializable

@Serializable
data class CheckResponse(
  val allowed: Boolean
)
