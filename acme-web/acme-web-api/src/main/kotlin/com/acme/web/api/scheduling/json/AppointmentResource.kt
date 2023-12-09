package com.acme.web.api.scheduling.json

import com.acme.web.api.json.HalLink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentResource(
  @SerialName("_links")
  val links: Links,
  val id: String,
  val state: AppointmentState,
  val period: Period
) {
  @Serializable
  data class Links(
    val self: HalLink,
    val client: HalLink,
    val practice: HalLink,
    val practitioner: HalLink,
    val cancel: HalLink,
    val markAttended: HalLink,
    val markUnattended: HalLink
  )
}
