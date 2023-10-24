package com.acme.web.api.security.keto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelationTuple(
  val namespace: String,
  @SerialName("object")
  val obj: String,
  val relation: String,
  @SerialName("subject_id")
  val subjectId: String? = null,
  @SerialName("subject_set")
  val subjectSet: RelationTuple? = null
) {
  constructor(
    namespace: String,
    obj: String,
    relation: String,
    subjectId: String
  ) : this(namespace, obj, relation, subjectId, null)

  constructor(
    namespace: String,
    obj: String,
    relation: String,
    subjectSet: RelationTuple
  ) : this(namespace, obj, relation, null, subjectSet)
}
