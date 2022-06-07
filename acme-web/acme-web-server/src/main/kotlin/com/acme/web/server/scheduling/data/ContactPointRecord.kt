package com.acme.web.server.scheduling.data

import java.time.Instant

data class ContactPointRecord(
  val value: String,
  val system: String,
  val verifiedAt: Instant?
)
