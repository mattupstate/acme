package com.acme.web.api.scheduling.json

import com.acme.serialization.InstantAsLongSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ContactPoint(
  val value: String,
  val system: ContactPointSystem,
  @Serializable(InstantAsLongSerializer::class)
  val verifiedAt: Instant?
) : Comparable<ContactPoint> {
  override fun compareTo(other: ContactPoint): Int =
    when {
      other == this -> 0
      system == other.system -> value.compareTo(other.value)
      else -> system.compareTo(other.system)
    }
}
