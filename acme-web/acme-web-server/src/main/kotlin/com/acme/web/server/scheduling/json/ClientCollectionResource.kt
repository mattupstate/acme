package com.acme.web.server.scheduling.json

import com.acme.web.server.json.hal.HalLink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientCollectionResource(
  @SerialName("_links")
  val links: Links,
  val total: Int
) {
  @Serializable
  data class Links(
    val self: HalLink,
    val items: List<HalLink> = emptyList()
  )

  companion object {
    fun withLinks(self: String, items: List<String>) =
      ClientCollectionResource(
        links = Links(
          self = HalLink(self),
          items = items.map(::HalLink)
        ),
        total = items.size
      )
  }
}
