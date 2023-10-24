package com.acme.web.app.security.keto

import kotlinx.serialization.Serializable

@Serializable
data class CheckResponse(
  val allowed: Boolean
)
