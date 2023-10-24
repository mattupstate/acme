package com.acme.web.api.scheduling.data

import java.time.Instant

data class AppointmentRecord(
  val id: String,
  val clientId: String,
  val practitionerId: String,
  val practiceId: String,
  val state: String,
  val periodStart: Instant?,
  val periodEnd: Instant?,
)
