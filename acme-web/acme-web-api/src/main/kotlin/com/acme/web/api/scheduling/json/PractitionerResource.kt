package com.acme.web.api.scheduling.json

import com.acme.web.api.json.HalLink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PractitionerResource(
  @SerialName("_links")
  val links: Links,
  val id: String,
  val names: List<HumanName>,
  val gender: Gender,
  val contactPoints: List<ContactPoint>
) {
  @Serializable
  data class Links(
    val self: HalLink
  )

  companion object {
    const val EXAMPLE_JSON_VALUE = """
      {
        "_links": {
          "self": {
            "href": "/practitioners/123"
          }
        },
        "id": "practitioner123",
        "names": [{
          "family": "Evans",
          "given": "Debbie",
          "suffix": "",
          "prefix": "",
          "period": {
            "start": null,
            "end": null
          }
        }],
        "gender": "UNKNOWN",
        "contactPoints": [{
          "system": "Email",
          "value": "hello@world.com",
          "verifiedAt": null
        }, {
          "system": "Phone",
          "value": "917-555-5555",
          "verifiedAt": null
        }]
      }
    """
  }
}
