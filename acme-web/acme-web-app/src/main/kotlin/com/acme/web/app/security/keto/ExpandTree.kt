package com.acme.web.app.security.keto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExpandTree(
  val children: List<ExpandTree>,
  @SerialName("subject_id")
  val subjectId: String?,
  @SerialName("subject_set")
  val subjectSet: RelationTuple?,
  val type: String,
)
