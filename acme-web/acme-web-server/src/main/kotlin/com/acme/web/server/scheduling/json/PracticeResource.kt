package com.acme.web.server.scheduling.json

import com.acme.web.server.json.hal.HalLink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PracticeResource(
  @SerialName("_links")
  val links: Links,
  val id: String,
  val name: String,
  val contactPoints: List<ContactPoint>,
) {
  @Serializable
  data class Links(
    val self: HalLink
  )
}
