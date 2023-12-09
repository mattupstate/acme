package com.acme.web.api.scheduling.json

import com.acme.web.api.json.HalLink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientResource(
  @SerialName("_links")
  val links: Links,
  val id: String,
  val names: List<HumanName>,
  val gender: Gender,
  val contactPoints: List<ContactPoint>,
) {
  @Serializable
  data class Links(
    val self: HalLink
  )
}
