package com.acme.web.server.scheduling.data

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class HumanNameRecord(
  val given: String,
  val family: String,
  val prefix: String,
  val suffix: String,
  val periodStart: Instant?,
  val periodEnd: Instant?,
) {
  constructor(
    given: String?,
    family: String?,
    prefix: String?,
    suffix: String?,
    periodStart: LocalDateTime?,
    periodEnd: LocalDateTime?
  ) :
    this(
      given!!,
      family!!,
      suffix!!,
      prefix!!,
      periodStart?.toInstant(ZoneOffset.UTC),
      periodEnd?.toInstant(ZoneOffset.UTC)
    )
}
