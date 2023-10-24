package com.acme.web.api.security.keto

import kotlinx.serialization.Serializable

@Serializable
data class CheckResponse(
  val allowed: Boolean
)
