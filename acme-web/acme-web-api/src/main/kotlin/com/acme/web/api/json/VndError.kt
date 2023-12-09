package com.acme.web.api.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VndError(
  @SerialName("_links")
  val links: Links = Links(),
  val message: String,
  val logRef: String? = null,
  val path: String? = null,
  val total: Int? = null,
  @SerialName("_embedded")
  val embedded: Embedded = Embedded()
) {
  @Serializable
  data class Links(
    val about: HalLink? = null,
    val describes: HalLink? = null,
    val help: HalLink? = null,
  )

  @Serializable
  data class Embedded(
    val errors: List<VndError> = emptyList()
  ) {
    constructor(vararg errors: VndError) : this(errors.toList())
  }
}
