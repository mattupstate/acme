package com.acme.web.api.scheduling.data

import java.time.Instant

data class HumanNameRecord(
  val given: String,
  val family: String,
  val prefix: String,
  val suffix: String,
  val periodStart: Instant?,
  val periodEnd: Instant?,
)
