package com.acme.scheduling

import com.acme.serialization.LocalDateTimeAsStringSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
sealed class Period {

  @Serializable
  data class Bounded(
    @Serializable(LocalDateTimeAsStringSerializer::class)
    val start: LocalDateTime,
    @Serializable(LocalDateTimeAsStringSerializer::class)
    val end: LocalDateTime
  ) : Period()

  @Serializable
  data class Unbounded(
    @Serializable(LocalDateTimeAsStringSerializer::class)
    val start: LocalDateTime
  ) : Period()

  @Serializable
  object Unknown : Period()
}
