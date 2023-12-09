package com.acme.web.api.json

import com.acme.serialization.URIAsStringSerializer
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class HalLink(
  @Serializable(with = URIAsStringSerializer::class)
  val href: URI,
  val name: String? = null,
  val title: String? = null,
  val type: String? = null,
  val hreflang: String? = null,
  val profile: String? = null,
  val deprecation: String? = null,
  val templated: Boolean? = null,
) {
  constructor(href: String) : this(URI(href))
}
