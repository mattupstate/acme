package com.acme.web.api.scheduling.json

import com.acme.serialization.InstantAsLongSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Period(
  @Serializable(InstantAsLongSerializer::class)
  val start: Instant?,
  @Serializable(InstantAsLongSerializer::class)
  val end: Instant?,
) : Comparable<Period> {
  override fun compareTo(other: Period): Int =
    when {
      start != null && start == other.start && end != null -> end.compareTo(other.end)
      start != null -> start.compareTo(start)
      else -> 0
    }
}
