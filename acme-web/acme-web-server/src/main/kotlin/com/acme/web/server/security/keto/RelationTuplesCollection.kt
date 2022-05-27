package com.acme.web.server.security.keto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelationTuplesCollection(
  @SerialName("next_page_token")
  val nextPageToken: String,
  @SerialName("relation_tuples")
  val relationTuples: List<RelationTuple>,
)
