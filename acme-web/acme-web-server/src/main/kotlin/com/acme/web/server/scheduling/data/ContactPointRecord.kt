package com.acme.web.server.scheduling.data

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ContactPointRecord(
  val value: String,
  val system: String,
  val verifiedAt: Instant?
) {
  constructor(value: String?, system: String?, verifiedAt: LocalDateTime?) :
    this(value!!, system!!, verifiedAt?.toInstant(ZoneOffset.UTC))
}
