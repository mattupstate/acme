package com.acme.web.api.scheduling.data

import java.time.Instant

data class ContactPointRecord(
  val value: String,
  val system: String,
  val verifiedAt: Instant?
)
